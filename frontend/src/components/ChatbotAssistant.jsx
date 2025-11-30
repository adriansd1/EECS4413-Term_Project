import React, { useState, useRef, useEffect } from 'react';
import { GoogleGenerativeAI } from "@google/generative-ai";
import { MessageCircle, X, Send, Bot, User, Sparkles, Lock, ArrowRight } from 'lucide-react';
import '../styles/AuctionStyle.css';

const GEMINI_API_KEY = process.env.REACT_APP_GEMINI_API_KEY || "";

const ChatAssistant = ({ token, userId, onNavigate }) => {
    const [isOpen, setIsOpen] = useState(false);
    const [messages, setMessages] = useState([
        { role: 'model', text: "Hi! I'm your Auction Assistant. 🤖\n\nI can help you list items or find deals. Try saying: 'Start a Dutch auction for a Gaming PC'." }
    ]);
    const [input, setInput] = useState("");
    const [isTyping, setIsTyping] = useState(false);
    const chatSessionRef = useRef(null);
    const messagesEndRef = useRef(null);

    // --- 1. DEFINE TOOLS ---
    const toolsDefinition = [
        {
            functionDeclarations: [
                {
                    name: "navigate_to_page",
                    description: "Navigates the user to a specific page.",
                    parameters: {
                        type: "OBJECT",
                        properties: {
                            // ✅ FIX: Updated enums to match App.js keys ('upload' instead of 'create')
                            page: { 
                                type: "STRING", 
                                enum: ["home", "catalogue", "upload", "auth"], 
                                description: "home=Home Page, catalogue=Browse Items, upload=Sell Item Page, auth=Sign In" 
                            }
                        },
                        required: ["page"]
                    }
                },
                {
                    name: "create_auction",
                    description: "Creates a new auction. Dutch auctions require extra fields.",
                    parameters: {
                        type: "OBJECT",
                        properties: {
                            title: { type: "STRING" },
                            description: { type: "STRING" },
                            category: { type: "STRING", enum: ["Electronics", "Furniture", "Art", "Other"] },
                            auctionType: { type: "STRING", enum: ["FORWARD", "DUTCH"], description: "MUST be 'DUTCH' if user mentions price drops." },
                            startingPrice: { type: "NUMBER" },
                            durationMinutes: { type: "NUMBER" },
                            minPrice: { type: "NUMBER" },
                            decreaseAmount: { type: "NUMBER" },
                            decreaseIntervalSeconds: { type: "NUMBER" }
                        },
                        required: ["title", "description", "category", "auctionType", "startingPrice", "durationMinutes"]
                    }
                },
                {
                    name: "get_item_details",
                    description: "Finds an item by name to get details.",
                    parameters: {
                        type: "OBJECT",
                        properties: { searchQuery: { type: "STRING" } },
                        required: ["searchQuery"]
                    }
                },
                {
                    name: "place_bid_by_name",
                    description: "Places a bid or buys an item.",
                    parameters: {
                        type: "OBJECT",
                        properties: {
                            itemName: { type: "STRING" },
                            amount: { type: "NUMBER", description: "Bid amount. Optional for Dutch (Buy Now)." }
                        },
                        required: ["itemName"]
                    }
                }
            ]
        }
    ];

    // --- 2. INITIALIZE GEMINI ---
    useEffect(() => {
        if (!GEMINI_API_KEY) return;
        const genAI = new GoogleGenerativeAI(GEMINI_API_KEY);
        
        const model = genAI.getGenerativeModel({ 
            model: "gemini-2.5-flash", 
            tools: toolsDefinition,
            systemInstruction: `You are the Auction404 Assistant.
            
            RULES:
            1. **Navigation:** Use 'navigate_to_page' with 'upload' to sell items, or 'catalogue' to browse.
            2. **Dutch Buying:** If user buys Dutch item, call 'place_bid_by_name' with amount=0.
            3. **Dutch Selling:** Ask for Min Price, Drop Amount, Interval. Map 'auctionType' to 'DUTCH'.
            4. **Context:** If user says "create it", assume they mean the auction you just discussed.`
        });
        
        chatSessionRef.current = model.startChat({ history: [] });
    }, [token]);

    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    }, [messages]);

    // --- 3. EXECUTE TOOLS ---
    const executeFunction = async (name, args) => {
        console.log(`🤖 Executing Tool: ${name}`, args);

        if (name === "navigate_to_page") {
            // Direct mapping to App.js state keys
            onNavigate(args.page);
            return { result: `Mapsd to ${args.page}` };
        }

        if (name === "create_auction") {
            if (!token) return { result: "Error: User not logged in." };
            try {
                const payload = {
                    title: args.title,
                    description: args.description,
                    type: args.category || "Other",
                    auctionType: args.auctionType, 
                    startingPrice: args.startingPrice,
                    durationMinutes: args.durationMinutes,
                    seller: "ChatBotUser",
                    imageUrl: "https://placehold.co/400x300?text=AI+Listing",
                    minPrice: args.minPrice || null,
                    decreaseAmount: args.decreaseAmount || null,
                    decreaseIntervalSeconds: args.decreaseIntervalSeconds || null
                };

                const response = await fetch('http://localhost:8080/api/auctions/create', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
                    body: JSON.stringify(payload)
                });

                if (response.ok) {
                    // ✅ FIX: Go to 'catalogue' to see the new item (since 'auctions' page was removed)
                    onNavigate('catalogue'); 
                    return { result: `Success! ${args.auctionType} Auction created.` };
                }
                return { result: `Failed: ${await response.text()}` };
            } catch (e) { return { result: "Network error" }; }
        }

        if (name === "place_bid_by_name") {
            if (!token) return { result: "Error: User must sign in." };
            try {
                const resList = await fetch('http://localhost:8080/api/catalogue/active');
                const items = await resList.json();
                const target = items.find(i => (i.title || i.name).toLowerCase().includes(args.itemName.toLowerCase()));
                
                if (!target) return { result: "Item not found." };

                const type = target.type || target.auctionType; 
                const isDutch = type === 'DUTCH';
                let finalAmount = args.amount;

                if (isDutch) {
                    finalAmount = 0.00; 
                } else {
                    if (!finalAmount) return { result: "Please specify a bid amount for this Forward auction." };
                }

                const resBid = await fetch('http://localhost:8080/api/bids/place', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
                    body: JSON.stringify({ 
                        auctionId: target.id, 
                        userId: parseInt(userId), 
                        bidAmount: finalAmount 
                    })
                });
                
                if (resBid.ok) {
                    // ✅ FIX: Go to 'catalogue' first. 
                    // Ideally we'd open the specific item but that requires passing the object up.
                    // For now, catalogue is safer than crashing.
                    onNavigate('catalogue'); 
                    return { result: isDutch ? `🎉 You bought the ${target.title || target.name}!` : `✅ Bid of $${finalAmount} placed!` };
                }
                return { result: `Rejected: ${await resBid.text()}` };

            } catch (e) { return { result: "Network Error" }; }
        }

        if (name === "get_item_details") {
             try {
                const res = await fetch('http://localhost:8080/api/catalogue/active');
                const items = await res.json();
                const found = items.find(i => (i.title || i.name).toLowerCase().includes(args.searchQuery.toLowerCase()));
                return found ? { result: "Found", info: found } : { result: "Not found" };
            } catch(e) { return { result: "API Error" }; }
        }

        return { result: "Function not found" };
    };

    const handleSend = async () => {
        if (!input.trim()) return;
        const userMsg = { role: 'user', text: input };
        setMessages(prev => [...prev, userMsg]);
        setInput("");
        setIsTyping(true);

        try {
            const result = await chatSessionRef.current.sendMessage(userMsg.text);
            const response = result.response;
            const functionCalls = response.functionCalls();

            if (functionCalls?.length > 0) {
                const call = functionCalls[0];
                const toolResult = await executeFunction(call.name, call.args);
                const finalResult = await chatSessionRef.current.sendMessage([{
                    functionResponse: { name: call.name, response: toolResult }
                }]);
                setMessages(prev => [...prev, { role: 'model', text: finalResult.response.text() }]);
            } else {
                setMessages(prev => [...prev, { role: 'model', text: response.text() }]);
            }
        } catch (error) {
            setMessages(prev => [...prev, { role: 'model', text: "Oops! Connection error." }]);
        } finally {
            setIsTyping(false);
        }
    };

    return (
        <div style={{ position: 'fixed', bottom: '30px', right: '30px', zIndex: 9999 }}>
            {!isOpen && (
                <button onClick={() => setIsOpen(true)} className="bg-gradient-to-r from-blue-600 to-indigo-600 text-white p-4 rounded-full shadow-2xl hover:scale-110 transition-transform flex items-center justify-center border-4 border-white">
                    <Sparkles size={28} />
                </button>
            )}
            {isOpen && (
                <div className="bg-white rounded-2xl shadow-2xl border border-gray-200 w-80 sm:w-96 flex flex-col h-[600px] overflow-hidden animate-fade-in-up">
                    <div className="bg-gradient-to-r from-blue-600 to-indigo-600 p-4 text-white flex justify-between items-center shadow-md">
                        <div className="flex items-center gap-3">
                            <div className="bg-white/20 p-2 rounded-full"><Bot size={24} className="text-white" /></div>
                            <div><h3 className="font-bold text-lg">AuctionBot</h3><div className="text-xs text-blue-200">Online</div></div>
                        </div>
                        <button onClick={() => setIsOpen(false)}><X size={20} /></button>
                    </div>
                    <div className="flex-1 p-4 overflow-y-auto bg-slate-50 space-y-4">
                        {messages.map((msg, i) => (
                            <div key={i} className={`flex ${msg.role === 'user' ? 'justify-end' : 'justify-start'}`}>
                                <div className={`max-w-[85%] p-3 rounded-2xl text-sm shadow-sm ${msg.role === 'user' ? 'bg-blue-600 text-white rounded-br-none' : 'bg-white border text-gray-800 rounded-bl-none'}`}>{msg.text}</div>
                            </div>
                        ))}
                        {isTyping && <div className="text-xs text-gray-500 ml-10">Thinking...</div>}
                        <div ref={messagesEndRef} />
                    </div>
                    <div className="p-4 bg-white border-t">
                        {token ? (
                            <div className="flex gap-2">
                                <input className="flex-1 bg-gray-100 rounded-full px-5 py-3 text-sm outline-none focus:ring-2 focus:ring-indigo-500" placeholder="Type a message..." value={input} onChange={(e) => setInput(e.target.value)} onKeyDown={(e) => e.key === 'Enter' && handleSend()} />
                                <button onClick={handleSend} disabled={!input.trim() || isTyping} className="bg-indigo-600 text-white p-3 rounded-full hover:bg-indigo-700 disabled:bg-gray-300"><Send size={18} /></button>
                            </div>
                        ) : (
                            <button onClick={() => onNavigate('auth')} className="w-full bg-blue-600 text-white py-2 rounded-lg text-sm font-bold">Sign In to Chat</button>
                        )}
                    </div>
                </div>
            )}
        </div>
    );
};

export default ChatAssistant;