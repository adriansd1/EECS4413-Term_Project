import React, { useState } from 'react';
import { Upload, DollarSign, Clock, Tag, Type, Calendar, AlertCircle, Image as ImageIcon } from 'lucide-react';

const CreateAuction = ({ token, onAuctionCreated }) => {
    
    // --- STATE MANAGEMENT ---
    const [auctionType, setAuctionType] = useState('FORWARD');
    const [formData, setFormData] = useState({
        title: '',
        description: '',
        startingPrice: '',
        minPrice: '',           // Dutch only
        decreaseAmount: '',     // Dutch only
        decreaseInterval: 60,   // Dutch only (seconds)
        imageUrl: '',
        endDate: ''             // User picks a date
    });

    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState({ type: '', text: '' });

    // --- HANDLERS ---
    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setMessage({ type: '', text: '' });

        // 1. Calculate Duration in Minutes
        const start = new Date();
        const end = new Date(formData.endDate);
        const diffMs = end - start;
        const durationMinutes = Math.floor(diffMs / 60000);

        if (durationMinutes <= 0) {
            setMessage({ type: 'error', text: 'End time must be in the future!' });
            setLoading(false);
            return;
        }

        // 2. Prepare Payload
        const payload = {
            title: formData.title,
            description: formData.description,
            startingPrice: parseFloat(formData.startingPrice),
            type: auctionType,
            durationMinutes: durationMinutes,
            seller: "CurrentUser", 
            imageUrl: formData.imageUrl || "https://placehold.co/400x300?text=No+Image"
        };

        // 3. Add Dutch Specifics
        if (auctionType === 'DUTCH') {
            payload.minPrice = parseFloat(formData.minPrice);
            payload.decreaseAmount = parseFloat(formData.decreaseAmount);
            payload.decreaseIntervalSeconds = parseInt(formData.decreaseInterval);
        }

        // 4. Send to Backend
        try {
            // ✅ CRITICAL FIX: Point to AuctionController, NOT CatalogueController
            // This ensures the item is added to BOTH tables (Catalogue & Auction)
            const response = await fetch('http://localhost:8080/api/auctions/create', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(payload)
            });

            if (response.ok) {
                setMessage({ type: 'success', text: '✅ Auction Started Successfully!' });
                // Redirect after success
                setTimeout(() => {
                    if (onAuctionCreated) onAuctionCreated();
                }, 1500);
            } else {
                const errorText = await response.text();
                setMessage({ type: 'error', text: '❌ Failed: ' + errorText });
            }
        } catch (error) {
            setMessage({ type: 'error', text: 'Network Error: ' + error.message });
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-slate-50 py-10 px-4 flex justify-center items-start">
            <div className="bg-white w-full max-w-3xl rounded-xl shadow-2xl overflow-hidden border border-slate-200">
                
                {/* --- HEADER --- */}
                <div className="bg-gradient-to-r from-blue-600 to-indigo-600 p-8 text-white">
                    <h2 className="text-3xl font-bold flex items-center gap-3">
                        <Upload size={28} /> Sell an Item
                    </h2>
                    <p className="text-blue-100 mt-2 text-lg">Create a new auction listing in the marketplace.</p>
                </div>

                <div className="p-8">
                    {/* MESSAGE BANNER */}
                    {message.text && (
                        <div className={`mb-6 p-4 rounded-lg flex items-center gap-3 text-sm font-medium ${message.type === 'error' ? 'bg-red-50 text-red-700 border border-red-200' : 'bg-green-50 text-green-700 border border-green-200'}`}>
                            <AlertCircle size={18} />
                            {message.text}
                        </div>
                    )}

                    <form onSubmit={handleSubmit} className="space-y-8">
                        
                        {/* SECTION 1: BASIC INFO */}
                        <div className="space-y-4">
                            <h3 className="text-xl font-semibold text-slate-800 border-b pb-2">Item Details</h3>
                            
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                                <div className="col-span-2">
                                    <label className="block text-sm font-semibold text-slate-700 mb-1">Title</label>
                                    <div className="relative">
                                        <Type className="absolute left-3 top-3 text-slate-400" size={18} />
                                        <input 
                                            name="title" 
                                            className="w-full pl-10 pr-4 py-3 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none transition" 
                                            placeholder="e.g. 1967 Shelby Mustang" 
                                            onChange={handleChange}
                                            required 
                                        />
                                    </div>
                                </div>

                                <div className="col-span-2">
                                    <label className="block text-sm font-semibold text-slate-700 mb-1">Description</label>
                                    <textarea 
                                        name="description" 
                                        className="w-full p-4 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none h-32 transition" 
                                        placeholder="Describe condition, history, features..." 
                                        onChange={handleChange}
                                        required 
                                    />
                                </div>

                                <div className="col-span-2">
                                    <label className="block text-sm font-semibold text-slate-700 mb-1">Image URL</label>
                                    <div className="relative">
                                        <ImageIcon className="absolute left-3 top-3 text-slate-400" size={18} />
                                        <input 
                                            name="imageUrl" 
                                            className="w-full pl-10 pr-4 py-3 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none transition" 
                                            placeholder="https://example.com/image.jpg" 
                                            onChange={handleChange}
                                        />
                                    </div>
                                </div>
                            </div>
                        </div>

                        {/* SECTION 2: AUCTION SETTINGS */}
                        <div className="space-y-4">
                            <h3 className="text-xl font-semibold text-slate-800 border-b pb-2">Auction Settings</h3>

                            {/* Auction Type Selector */}
                            <div>
                                <label className="block text-sm font-semibold text-slate-700 mb-2">Auction Type</label>
                                <div className="grid grid-cols-2 gap-4">
                                    <button
                                        type="button"
                                        onClick={() => setAuctionType('FORWARD')}
                                        className={`py-3 px-4 rounded-lg border-2 font-semibold transition ${auctionType === 'FORWARD' ? 'border-blue-600 bg-blue-50 text-blue-700' : 'border-slate-200 text-slate-600 hover:border-blue-300'}`}
                                    >
                                        📈 Forward Auction
                                        <span className="block text-xs font-normal mt-1 text-slate-500">Price goes UP (Standard)</span>
                                    </button>
                                    <button
                                        type="button"
                                        onClick={() => setAuctionType('DUTCH')}
                                        className={`py-3 px-4 rounded-lg border-2 font-semibold transition ${auctionType === 'DUTCH' ? 'border-blue-600 bg-blue-50 text-blue-700' : 'border-slate-200 text-slate-600 hover:border-blue-300'}`}
                                    >
                                        📉 Dutch Auction
                                        <span className="block text-xs font-normal mt-1 text-slate-500">Price goes DOWN</span>
                                    </button>
                                </div>
                            </div>

                            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                                {/* Starting Price */}
                                <div>
                                    <label className="block text-sm font-semibold text-slate-700 mb-1">Starting Price ($)</label>
                                    <div className="relative">
                                        <DollarSign className="absolute left-3 top-3 text-slate-400" size={18} />
                                        <input 
                                            type="number" step="0.01" name="startingPrice" 
                                            className="w-full pl-10 pr-4 py-3 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none" 
                                            placeholder="0.00" 
                                            onChange={handleChange}
                                            required 
                                        />
                                    </div>
                                </div>

                                {/* End Date (Duration) */}
                                <div>
                                    <label className="block text-sm font-semibold text-slate-700 mb-1">End Date & Time</label>
                                    <div className="relative">
                                        <Calendar className="absolute left-3 top-3 text-slate-400" size={18} />
                                        <input 
                                            type="datetime-local" name="endDate" 
                                            className="w-full pl-10 pr-4 py-3 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none" 
                                            onChange={handleChange}
                                            required 
                                        />
                                    </div>
                                </div>
                            </div>

                            {/* CONDITIONAL: DUTCH FIELDS */}
                            {auctionType === 'DUTCH' && (
                                <div className="bg-slate-50 p-6 rounded-lg border border-slate-200 grid grid-cols-1 md:grid-cols-3 gap-6 animate-fadeIn">
                                    <div>
                                        <label className="block text-xs font-bold text-slate-500 uppercase mb-1">Lowest Price ($)</label>
                                        <input 
                                            type="number" step="0.01" name="minPrice" 
                                            className="w-full p-2 border rounded focus:ring-2 focus:ring-blue-500 outline-none" 
                                            placeholder="Stop at..." 
                                            onChange={handleChange}
                                            required 
                                        />
                                    </div>
                                    <div>
                                        <label className="block text-xs font-bold text-slate-500 uppercase mb-1">Drop Amount ($)</label>
                                        <input 
                                            type="number" step="0.01" name="decreaseAmount" 
                                            className="w-full p-2 border rounded focus:ring-2 focus:ring-blue-500 outline-none" 
                                            placeholder="Decrease by..." 
                                            onChange={handleChange}
                                            required 
                                        />
                                    </div>
                                    <div>
                                        <label className="block text-xs font-bold text-slate-500 uppercase mb-1">Interval (Seconds)</label>
                                        <input 
                                            type="number" name="decreaseInterval" 
                                            defaultValue={60}
                                            className="w-full p-2 border rounded focus:ring-2 focus:ring-blue-500 outline-none" 
                                            onChange={handleChange}
                                            required 
                                        />
                                    </div>
                                </div>
                            )}
                        </div>

                        {/* SUBMIT BUTTON */}
                        <div className="pt-4">
                            <button 
                                type="submit" 
                                disabled={loading}
                                className="w-full bg-blue-600 hover:bg-blue-700 text-white text-lg font-bold py-4 rounded-xl transition-all shadow-lg disabled:bg-gray-400 flex justify-center items-center gap-2"
                            >
                                {loading ? (
                                    <>Processing...</>
                                ) : (
                                    <>
                                        <Tag size={20} /> Create Auction
                                    </>
                                )}
                            </button>
                        </div>

                    </form>
                </div>
            </div>
        </div>
    );
};

export default CreateAuction;