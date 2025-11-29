import React, { useState, useRef, useEffect } from 'react';
import { GoogleGenerativeAI } from "@google/generative-ai";
import { MessageCircle, X, Send, Bot, User, Sparkles, Lock, ArrowRight } from 'lucide-react';
import '../styles/AuctionStyle.css';

// 🔴 REPLACE WITH YOUR ACTUAL KEY
const GEMINI_API_KEY = "AIzaSyDfLn7WInvGrJAJ4myDTuV6KJ7qFCmVGJ0"; 

const ChatAssistant = ({ token, userId, onNavigate }) => {
    const [isOpen, setIsOpen] = useState(false);
    const [messages, setMessages] = useState([
        { role: 'model', text: "Hi! I'm your Auction Assistant. 🤖\n\nI can help you list items or find deals. Try saying: 'Start a Dutch auction for a Gaming PC'." }
    ]);
    const [input, setInput] = useState("");
    const [isTyping, setIsTyping] = useState(false);
    const chatSessionRef = useRef(null);
    const messagesEndRef = useRef(null);

    // --- 1. DEFINE TOOLS (Updated with Dutch Params) ---
    const toolsDefinition = [
        {
            functionDeclarations: [
                {
                    name: "navigate_to_page",
                    description: "Navigates the user to a specific page.",
                    parameters: {
                        type: "OBJECT",
                        properties: {
                            page: { type: "STRING", enum: ["home", "catalogue", "auctions", "create", "auth"] }
                        },
                        required: ["page"]
                    }
                },
                {
                    name: "create_auction",
                    description: "Creates a new auction listing. Dutch auctions require extra fields.",
                    parameters: {
                        type: "OBJECT",
                        properties: {
                            title: { type: "STRING" },
                            description: { type: "STRING" },
                            startingPrice: { type: "NUMBER" },
                            type: { type: "STRING", enum: ["FORWARD", "DUTCH"] },
                            durationMinutes: { type: "NUMBER" },
                            // ✅ NEW PARAMS FOR DUTCH
                            minPrice: { type: "NUMBER", description: "Lowest price for Dutch auction" },
                            decreaseAmount: { type: "NUMBER", description: "Amount price drops by" },
                            decreaseIntervalSeconds: { type: "NUMBER", description: "Seconds between price drops" }
                        },
                        required: ["title", "description", "startingPrice", "type", "durationMinutes"]
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
                    description: "Places a bid on an item found by name.",
                    parameters: {
                        type: "OBJECT",
                        properties: {
                            itemName: { type: "STRING" },
                            amount: { type: "NUMBER" }
                        },
                        required: ["itemName", "amount"]
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
            systemInstruction: `You are a witty and helpful Auction Assistant for 'Auction404'. 
            Current Date: ${new Date().toLocaleString()}.
            
            RULES:
            1. **Dutch Auctions:** If user wants a DUTCH auction, you MUST ask for: Minimum Price, Drop Amount, and Interval (seconds). Do not call 'create_auction' until you have these.
            2. **Bidding:** Locate ID internally using 'place_bid_by_name'.
            3. **Tone:** Be enthusiastic, professional, but fun. Use emojis occasionally.`
        });
        chatSessionRef.current = model.startChat({ history: [] });
    }, []);

    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    }, [messages]);

    // --- 3. EXECUTE TOOLS ---
    const executeFunction = async (name, args) => {
        console.log(`🤖 AI Tool: ${name}`, args);

        if (name === "navigate_to_page") {
            onNavigate(args.page);
            return { result: `Mapsd to ${args.page}` };
        }

        if (name === "create_auction") {
            if (!token) return { result: "Error: User not logged in." };
            try {
                // ✅ Pass all args including Dutch params
                const response = await fetch('http://localhost:8080/api/auctions/create', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
                    body: JSON.stringify({ 
                        ...args, 
                        seller: "ChatBotUser", 
                        imageUrl: "https://placehold.co/400x300?text=AI+Listing" 
                    })
                });
                if (response.ok) {
                    onNavigate('catalogue');
                    return { result: "Success! Item created and user moved to catalogue." };
                }
                return { result: `Failed: ${await response.text()}` };
            } catch (e) { return { result: "Network error" }; }
        }

        if (name === "get_item_details") {
            try {
                const res = await fetch('http://localhost:8080/api/catalogue/active');
                const items = await res.json();
                const found = items.find(i => (i.title || i.name).toLowerCase().includes(args.searchQuery.toLowerCase()));
                return found ? { result: "Found", info: found } : { result: "Not found" };
            } catch(e) { return { result: "API Error" }; }
        }

        if (name === "place_bid_by_name") {
            if (!token) return { result: "Error: User must sign in." };
            try {
                const resList = await fetch('http://localhost:8080/api/catalogue/active');
                const items = await resList.json();
                const target = items.find(i => (i.title || i.name).toLowerCase().includes(args.itemName.toLowerCase()));
                if (!target) return { result: "Item not found" };

                const resBid = await fetch('http://localhost:8080/api/bids/place', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
                    body: JSON.stringify({ auctionId: target.id, userId: parseInt(userId), bidAmount: parseFloat(args.amount) })
                });
                
                if (resBid.ok) return { result: "Bid Placed Successfully!" };
                return { result: `Bid Rejected: ${await resBid.text()}` };
            } catch (e) { return { result: "Network Error" }; }
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
            setMessages(prev => [...prev, { role: 'model', text: "Oops! My brain froze. Try again? 🥶" }]);
        } finally {
            setIsTyping(false);
        }
    };

    return (
        <div style={{ position: 'fixed', bottom: '30px', right: '30px', zIndex: 9999 }}>
            
            {/* 1. CLOSED BUBBLE (Animated) */}
            {!isOpen && (
                <button 
                    onClick={() => setIsOpen(true)}
                    className="bg-gradient-to-r from-blue-600 to-indigo-600 text-white p-4 rounded-full shadow-2xl hover:scale-110 transition-transform duration-300 animate-bounce-slow flex items-center justify-center border-4 border-white"
                >
                    <Sparkles size={28} />
                </button>
            )}

            {/* 2. OPEN WINDOW */}
            {isOpen && (
                <div className="bg-white rounded-2xl shadow-2xl border border-gray-200 w-80 sm:w-96 flex flex-col h-[600px] overflow-hidden transition-all animate-fade-in-up">
                    
                    {/* Header */}
                    <div className="bg-gradient-to-r from-blue-600 to-indigo-600 p-4 text-white flex justify-between items-center shadow-md">
                        <div className="flex items-center gap-3">
                            <div className="bg-white/20 p-2 rounded-full">
                                <Bot size={24} className="text-white" />
                            </div>
                            <div>
                                <h3 className="font-bold text-lg">AuctionBot</h3>
                                <div className="flex items-center gap-1 text-xs text-blue-200">
                                    <span className="w-2 h-2 bg-green-400 rounded-full animate-pulse"></span>
                                    Online
                                </div>
                            </div>
                        </div>
                        <button onClick={() => setIsOpen(false)} className="hover:bg-white/20 p-1 rounded-full transition">
                            <X size={20} />
                        </button>
                    </div>

                    {/* Chat Area */}
                    <div className="flex-1 p-4 overflow-y-auto bg-slate-50 space-y-4 scroll-smooth">
                        {messages.map((msg, i) => (
                            <div key={i} className={`flex ${msg.role === 'user' ? 'justify-end' : 'justify-start'} animate-slide-in`}>
                                {msg.role === 'model' && (
                                    <div className="w-8 h-8 rounded-full bg-indigo-100 flex items-center justify-center mr-2 flex-shrink-0">
                                        <Bot size={16} className="text-indigo-600" />
                                    </div>
                                )}
                                <div className={`max-w-[80%] p-3 rounded-2xl text-sm shadow-sm ${
                                    msg.role === 'user' 
                                    ? 'bg-blue-600 text-white rounded-br-none' 
                                    : 'bg-white border border-gray-100 text-gray-800 rounded-bl-none'
                                }`}>
                                    {msg.text}
                                </div>
                                {msg.role === 'user' && (
                                    <div className="w-8 h-8 rounded-full bg-blue-100 flex items-center justify-center ml-2 flex-shrink-0">
                                        <User size={16} className="text-blue-600" />
                                    </div>
                                )}
                            </div>
                        ))}
                        
                        {/* Typing Indicator */}
                        {isTyping && (
                            <div className="flex justify-start items-center gap-2 ml-10">
                                <div className="flex space-x-1">
                                    <div className="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style={{ animationDelay: '0s' }}></div>
                                    <div className="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style={{ animationDelay: '0.2s' }}></div>
                                    <div className="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style={{ animationDelay: '0.4s' }}></div>
                                </div>
                            </div>
                        )}
                        <div ref={messagesEndRef} />
                    </div>

                    {/* Input Area (Auth Gated) */}
                    <div className="p-4 bg-white border-t border-gray-100">
                        {token ? (
                            <div className="flex gap-2 items-center">
                                <input 
                                    className="flex-1 bg-gray-100 text-gray-800 border-none rounded-full px-5 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 transition-all"
                                    placeholder="Type a message..."
                                    value={input}
                                    onChange={(e) => setInput(e.target.value)}
                                    onKeyDown={(e) => e.key === 'Enter' && handleSend()}
                                />
                                <button 
                                    onClick={handleSend}
                                    disabled={!input.trim() || isTyping}
                                    className="bg-indigo-600 text-white p-3 rounded-full hover:bg-indigo-700 disabled:bg-gray-300 disabled:cursor-not-allowed shadow-md transition-all hover:scale-105"
                                >
                                    <Send size={18} />
                                </button>
                            </div>
                        ) : (
                            // ✅ AUTH REDIRECT UI
                            <div className="flex flex-col items-center justify-center gap-3 p-2 bg-blue-50 rounded-xl border border-blue-100">
                                <div className="flex items-center gap-2 text-blue-800 text-sm font-medium">
                                    <Lock size={16} />
                                    <span>Sign in to chat & bid</span>
                                </div>
                                <button 
                                    onClick={() => onNavigate('auth')}
                                    className="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-lg text-sm flex items-center justify-center gap-2 transition-colors"
                                >
                                    Go to Sign In <ArrowRight size={16}/>
                                </button>
                            </div>
                        )}
                    </div>
                </div>
            )}
        </div>
    );
};

export default ChatAssistant;