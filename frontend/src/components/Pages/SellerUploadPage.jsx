import React, { useState } from "react";
import { AlertCircle, CheckCircle, Hammer, Clock } from "lucide-react";

const CATALOGUE_API = "http://localhost:8080/api/catalogue/upload";
const AUCTION_API   = "http://localhost:8080/api/auctions/create";

export default function SellerUploadPage({ userId, token, navigateTo }) {

    const [form, setForm] = useState({
        title: "",
        description: "",
        type: "",
        startingPrice: "",
        durationHours: "",
        seller: "",
        imageUrl: "",
        auctionType: "COMPETITIVE"
    });

    const [loading, setLoading] = useState(false);
    const [errorMsg, setErrorMsg] = useState("");
    const [successMsg, setSuccessMsg] = useState("");

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const validate = () => {
        for (let key in form) {
            if (!form[key]) {
                setErrorMsg("All fields are mandatory.");
                return false;
            }
        }
        if (isNaN(form.startingPrice) || Number(form.startingPrice) <= 0) {
            setErrorMsg("Starting price must be positive.");
            return false;
        }
        if (isNaN(form.durationHours) || Number(form.durationHours) <= 0) {
            setErrorMsg("Duration must be positive.");
            return false;
        }
        return true;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setErrorMsg("");
        setSuccessMsg("");

        if (!validate()) return;

        setLoading(true);

        const durationMinutes = Number(form.durationHours) * 60;

        try {
            // 1) Upload to CATALOGUE
            const catalogueRes = await fetch(CATALOGUE_API, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    title: form.title,
                    description: form.description,
                    type: form.type,
                    startingPrice: Number(form.startingPrice),
                    durationMinutes: durationMinutes,
                    seller: form.seller,
                    imageUrl: form.imageUrl
                })
            });

            if (!catalogueRes.ok) throw new Error("Catalogue upload failed");

            // 2) Upload to AUCTIONS
            const auctionRes = await fetch(AUCTION_API, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    title: form.title,
                    description: form.description,
                    type: form.type,
                    startingPrice: Number(form.startingPrice),
                    durationMinutes,
                    auctionType: form.auctionType,
                    seller: form.seller,
                    imageUrl: form.imageUrl,
                    minPrice: form.auctionType === "DUTCH" ?  form.minPrice : null,
                    decreaseAmount: form.auctionType === "DUTCH" ? form.decreaseAmount : null,
                    decreaseIntervalSeconds: form.auctionType === "DUTCH" ? form.decreaseIntervalSeconds : null
                })
            });


            if (!auctionRes.ok) throw new Error("Auction creation failed");

            setSuccessMsg("Item uploaded and auction created successfully!");

            // Reset form
            setForm({
                title: "",
                description: "",
                type: "",
                startingPrice: "",
                durationHours: "",
                seller: "",
                imageUrl: "",
                auctionType: "COMPETITIVE"
            });

        } catch (err) {
            setErrorMsg(err.message);
        }

        setLoading(false);
    };

    return (
        <div className="min-h-screen bg-gray-100 flex justify-center p-8">

            <div className="max-w-3xl w-full bg-white shadow-xl rounded-2xl p-8">

                <h1 className="text-3xl font-bold mb-6 flex items-center gap-3">
                    <Hammer size={30} /> Upload Auction Item
                </h1>

                {/* Error Banner */}
                {errorMsg && (
                    <div className="flex items-center gap-3 p-4 bg-red-100 text-red-700 rounded mb-4">
                        <AlertCircle size={20} />
                        <span>{errorMsg}</span>
                    </div>
                )}

                {/* Success Banner */}
                {successMsg && (
                    <div className="flex items-center gap-3 p-4 bg-green-100 text-green-700 rounded mb-4">
                        <CheckCircle size={20} />
                        <span>{successMsg}</span>
                    </div>
                )}

                {/* FORM */}
                <form onSubmit={handleSubmit} className="space-y-6">

                    {/* Title */}
                    <div>
                        <label className="block mb-1 font-semibold">Item Title *</label>
                        <input
                            type="text"
                            name="title"
                            value={form.title}
                            onChange={handleChange}
                            className="w-full p-3 border rounded-lg"
                            placeholder="Vintage Clock"
                        />
                    </div>

                    {/* Description */}
                    <div>
                        <label className="block mb-1 font-semibold">Description *</label>
                        <textarea
                            name="description"
                            value={form.description}
                            onChange={handleChange}
                            className="w-full p-3 border rounded-lg h-28"
                            placeholder="Describe the item..."
                        ></textarea>
                    </div>

                    {/* Category */}
                    <div>
                        <label className="block mb-1 font-semibold">Category *</label>
                        <select
                            name="type"
                            value={form.type}
                            onChange={handleChange}
                            className="w-full p-3 border rounded-lg"
                        >
                            <option value="">Select category</option>
                            <option value="Electronics">Electronics</option>
                            <option value="Furniture">Furniture</option>
                            <option value="Antique">Antique</option>
                            <option value="Art">Art</option>
                            <option value="Collectible">Collectible</option>
                            <option value="Other">Other</option>
                        </select>
                    </div>

                    {/* Starting Price */}
                    <div>
                        <label className="block mb-1 font-semibold">Starting Price ($) *</label>
                        <input
                            type="number"
                            name="startingPrice"
                            value={form.startingPrice}
                            onChange={handleChange}
                            className="w-full p-3 border rounded-lg"
                        />
                    </div>

                    {/* Duration */}
                    <div>
                        <label className="block mb-1 font-semibold">Duration (Hours) *</label>
                        <div className="flex gap-3 items-center">
                            <Clock size={20} className="text-gray-500" />
                            <input
                                type="number"
                                name="durationHours"
                                value={form.durationHours}
                                onChange={handleChange}
                                className="w-full p-3 border rounded-lg"
                                placeholder="48"
                            />
                        </div>
                    </div>

                    {/* Seller */}
                    <div>
                        <label className="block mb-1 font-semibold">Seller *</label>
                        <input
                            type="text"
                            name="seller"
                            value={form.seller}
                            onChange={handleChange}
                            className="w-full p-3 border rounded-lg"
                            placeholder="Your seller name"
                        />
                    </div>

                    {/* Image URL */}
                    <div>
                        <label className="block mb-1 font-semibold">Image URL *</label>
                        <input
                            type="text"
                            name="imageUrl"
                            value={form.imageUrl}
                            onChange={handleChange}
                            className="w-full p-3 border rounded-lg"
                            placeholder="https://example.com/image.jpg"
                        />
                    </div>

                    {/* Auction Type */}
                    <div>
                        <label className="block mb-1 font-semibold">Auction Type *</label>
                        <select
                            name="auctionType"
                            value={form.auctionType}
                            onChange={handleChange}
                            className="w-full p-3 border rounded-lg"
                        >
                            <option value="COMPETITIVE">Competitive Auction</option>
                            <option value="DUTCH">Dutch Auction</option>
                        </select>
                    </div>

                    {/* Submit */}
                    <button
                        type="submit"
                        disabled={loading}
                        className="w-full bg-blue-600 hover:bg-blue-700 text-white p-3 rounded-xl text-lg font-semibold"
                    >
                        {loading ? "Submitting..." : "Create Auction + Publish"}
                    </button>

                </form>
            </div>
        </div>
    );
}
