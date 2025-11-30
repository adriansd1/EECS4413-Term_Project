import React, { useState } from "react";
import { AlertCircle, CheckCircle, Hammer, Clock, DollarSign } from "lucide-react";

// ✅ FIX 1: Use ONLY the Auction Creation Endpoint (It handles both tables)
const AUCTION_API = "http://localhost:8080/api/auctions/create";

export default function SellerUploadPage({ userId, token }) {

    const [form, setForm] = useState({
        title: "",
        description: "",
        type: "Electronics", // Category
        startingPrice: "",
        durationHours: "",
        seller: "",
        imageUrl: "",
        auctionType: "FORWARD", // Default to Forward
        
        // Dutch Specifics
        minPrice: "",
        decreaseAmount: "",
        decreaseIntervalSeconds: 60
    });

    const [loading, setLoading] = useState(false);
    const [errorMsg, setErrorMsg] = useState("");
    const [successMsg, setSuccessMsg] = useState("");

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const validate = () => {
        if (!form.title || !form.description || !form.startingPrice || !form.durationHours) {
            setErrorMsg("Please fill in all required fields.");
            return false;
        }
        if (Number(form.startingPrice) <= 0) {
            setErrorMsg("Starting price must be positive.");
            return false;
        }
        
        // Dutch Validation
        if (form.auctionType === 'DUTCH') {
            if (!form.minPrice || !form.decreaseAmount) {
                setErrorMsg("Dutch Auctions require Min Price and Drop Amount.");
                return false;
            }
            if (Number(form.minPrice) >= Number(form.startingPrice)) {
                setErrorMsg("Min Price must be lower than Starting Price.");
                return false;
            }
        }
        
        return true;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setErrorMsg("");
        setSuccessMsg("");

        if (!validate()) return;
        setLoading(true);

        const durationMinutes = Math.floor(Number(form.durationHours) * 60);

        try {
            // ✅ FIX 2: Construct the correct payload for AuctionController
            const payload = {
                title: form.title,
                description: form.description,
                type: form.auctionType, // "FORWARD" or "DUTCH"
                startingPrice: Number(form.startingPrice),
                durationMinutes: durationMinutes,
                seller: form.seller || "Unknown Seller",
                imageUrl: form.imageUrl || "https://placehold.co/400x300?text=No+Image"
            };

            // Add Dutch fields if needed
            if (form.auctionType === 'DUTCH') {
                payload.minPrice = Number(form.minPrice);
                payload.decreaseAmount = Number(form.decreaseAmount);
                payload.decreaseIntervalSeconds = Number(form.decreaseIntervalSeconds);
            }

            // ✅ FIX 3: Single Fetch Call
            const response = await fetch(AUCTION_API, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify(payload)
            });

            if (!response.ok) {
                const txt = await response.text();
                throw new Error(txt || "Failed to create auction");
            }

            setSuccessMsg("✅ Auction created successfully!");
            
            // Clear form
            setForm({
                title: "", description: "", type: "Electronics",
                startingPrice: "", durationHours: "", seller: "", imageUrl: "",
                auctionType: "FORWARD", minPrice: "", decreaseAmount: "", decreaseIntervalSeconds: 60
            });

        } catch (err) {
            setErrorMsg(err.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-gray-100 flex justify-center p-8">
            <div className="max-w-3xl w-full bg-white shadow-xl rounded-2xl p-8">

                <h1 className="text-3xl font-bold mb-6 flex items-center gap-3 text-gray-800">
                    <Hammer size={30} className="text-blue-600" /> Upload Auction Item
                </h1>

                {errorMsg && (
                    <div className="flex items-center gap-3 p-4 bg-red-100 text-red-700 rounded mb-4">
                        <AlertCircle size={20} /> <span>{errorMsg}</span>
                    </div>
                )}

                {successMsg && (
                    <div className="flex items-center gap-3 p-4 bg-green-100 text-green-700 rounded mb-4">
                        <CheckCircle size={20} /> <span>{successMsg}</span>
                    </div>
                )}

                <form onSubmit={handleSubmit} className="space-y-6">
                    
                    {/* Basic Info */}
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <div className="col-span-2">
                            <label className="block mb-1 font-semibold text-gray-700">Item Title *</label>
                            <input name="title" value={form.title} onChange={handleChange} className="w-full p-3 border rounded-lg focus:ring-2 focus:ring-blue-500 outline-none" placeholder="Vintage Clock" />
                        </div>

                        <div className="col-span-2">
                            <label className="block mb-1 font-semibold text-gray-700">Description *</label>
                            <textarea name="description" value={form.description} onChange={handleChange} className="w-full p-3 border rounded-lg h-24 focus:ring-2 focus:ring-blue-500 outline-none" placeholder="Describe the item..." />
                        </div>

                        <div>
                            <label className="block mb-1 font-semibold text-gray-700">Category</label>
                            <select name="type" value={form.type} onChange={handleChange} className="w-full p-3 border rounded-lg bg-white">
                                <option value="Electronics">Electronics</option>
                                <option value="Furniture">Furniture</option>
                                <option value="Art">Art</option>
                                <option value="Other">Other</option>
                            </select>
                        </div>

                        <div>
                            <label className="block mb-1 font-semibold text-gray-700">Seller Name</label>
                            <input name="seller" value={form.seller} onChange={handleChange} className="w-full p-3 border rounded-lg" placeholder="Your Name" />
                        </div>
                    </div>

                    {/* Pricing & Time */}
                    <div className="bg-blue-50 p-6 rounded-xl border border-blue-100 space-y-4">
                        <h3 className="font-bold text-blue-800 text-lg flex items-center gap-2"><Clock size={20}/> Auction Settings</h3>
                        
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                            <div>
                                <label className="block mb-1 font-semibold text-gray-700">Auction Type *</label>
                                <select name="auctionType" value={form.auctionType} onChange={handleChange} className="w-full p-3 border rounded-lg bg-white font-medium">
                                    <option value="FORWARD">📈 Competitive (Forward)</option>
                                    <option value="DUTCH">📉 Dutch (Price Drop)</option>
                                </select>
                            </div>

                            <div>
                                <label className="block mb-1 font-semibold text-gray-700">Duration (Hours) *</label>
                                <input type="number" name="durationHours" value={form.durationHours} onChange={handleChange} className="w-full p-3 border rounded-lg" placeholder="24" />
                            </div>

                            <div>
                                <label className="block mb-1 font-semibold text-gray-700">Starting Price ($) *</label>
                                <div className="relative">
                                    <DollarSign className="absolute left-3 top-3 text-gray-400" size={20} />
                                    <input type="number" name="startingPrice" value={form.startingPrice} onChange={handleChange} className="w-full pl-10 p-3 border rounded-lg" placeholder="100.00" />
                                </div>
                            </div>
                        </div>

                        {/* ✅ DUTCH SETTINGS (Conditional) */}
                        {form.auctionType === 'DUTCH' && (
                            <div className="mt-4 p-4 bg-white rounded-lg border border-blue-200 animate-fade-in-up">
                                <h4 className="font-bold text-gray-700 mb-3 text-sm uppercase tracking-wide">Dutch Auction Rules</h4>
                                <div className="grid grid-cols-3 gap-4">
                                    <div>
                                        <label className="block text-xs font-bold text-gray-500 mb-1">Lowest Price ($)</label>
                                        <input type="number" name="minPrice" value={form.minPrice} onChange={handleChange} className="w-full p-2 border rounded" placeholder="50" />
                                    </div>
                                    <div>
                                        <label className="block text-xs font-bold text-gray-500 mb-1">Drop Amount ($)</label>
                                        <input type="number" name="decreaseAmount" value={form.decreaseAmount} onChange={handleChange} className="w-full p-2 border rounded" placeholder="10" />
                                    </div>
                                    <div>
                                        <label className="block text-xs font-bold text-gray-500 mb-1">Interval (Secs)</label>
                                        <input type="number" name="decreaseIntervalSeconds" value={form.decreaseIntervalSeconds} onChange={handleChange} className="w-full p-2 border rounded" />
                                    </div>
                                </div>
                            </div>
                        )}
                    </div>

                    <div>
                        <label className="block mb-1 font-semibold text-gray-700">Image URL</label>
                        <input name="imageUrl" value={form.imageUrl} onChange={handleChange} className="w-full p-3 border rounded-lg" placeholder="https://..." />
                    </div>

                    <button type="submit" disabled={loading} className="w-full bg-blue-600 hover:bg-blue-700 text-white p-4 rounded-xl text-lg font-bold shadow-lg transition-all transform hover:scale-[1.01] disabled:opacity-50 disabled:cursor-not-allowed">
                        {loading ? "Creating..." : "Launch Auction 🚀"}
                    </button>

                </form>
            </div>
        </div>
    );
}