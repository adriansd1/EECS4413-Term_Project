import React, { useEffect, useState } from "react";
import { Clock3, ArrowLeft, Tag } from "lucide-react";

const CatalogueItemPage = ({ item, navigateTo }) => {
    const [live, setLive] = useState(item);

    // Auto-refresh every 1 sec
    useEffect(() => {
        const interval = setInterval(async () => {
            try {
                const r = await fetch(`http://localhost:8080/api/catalogue/${item.id}`);
                if (!r.ok) return;

                const data = await r.json();
                setLive((old) => ({
                    ...old,
                    currentBid: data.currentBid,
                    timeLeft: data.timeLeft,
                    closed: data.closed
                }));
            } catch {}
        }, 1000);

        return () => clearInterval(interval);
    }, [item.id]);

    const money = (n) => `$${Number(n).toFixed(2)}`;

    return (
        <div className="min-h-screen bg-slate-50 p-6">
            {/* Back */}
            <button
                onClick={() => navigateTo("catalogue")}
                className="flex items-center gap-2 text-blue-600 hover:underline mb-6"
            >
                <ArrowLeft size={18} />
                Back to Catalogue
            </button>

            <div className="max-w-4xl mx-auto bg-white shadow-lg rounded-xl p-6 grid grid-cols-1 md:grid-cols-2 gap-8">

                {/* IMAGE */}
                <div>
                    <img
                        src={live.imageUrl}
                        alt={live.name}
                        className="w-full h-80 object-cover rounded-xl border"
                    />
                </div>

                {/* DETAILS */}
                <div className="flex flex-col gap-4">

                    <h1 className="text-3xl font-bold text-slate-900">{live.name}</h1>

                    <span className="inline-block px-3 py-1 bg-blue-100 text-blue-700 text-sm rounded-full flex items-center gap-1 w-fit">
                        <Tag size={14} /> {live.type}
                    </span>

                    <p className="text-slate-600 text-md leading-relaxed">
                        {live.description}
                    </p>

                    {/* PRICE + TIMER */}
                    <div className="bg-slate-50 border rounded-xl p-4 flex flex-col gap-2">
                        <p className="text-slate-500 text-sm">Current Bid</p>
                        <p className="text-3xl font-extrabold">{money(live.currentBid)}</p>

                        <div className="flex items-center gap-2 text-amber-700 text-sm bg-amber-50 px-3 py-1 rounded-full w-fit">
                            <Clock3 size={16} />
                            {live.timeLeft || "—"}
                        </div>
                    </div>

                    {/* ACTION BUTTON */}
                    <button
                        onClick={() => navigateTo("auctionFromCatalogue", live)}
                        className="mt-3 bg-blue-600 hover:bg-blue-700 text-white py-3 rounded-xl text-lg font-semibold transition"
                    >
                        Go to Live Auction
                    </button>
                </div>
            </div>
        </div>
    );
};

export default CatalogueItemPage;
