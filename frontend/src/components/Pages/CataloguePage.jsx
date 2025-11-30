import React, { useCallback, useEffect, useMemo, useState } from "react";
import { Search, Filter, Tag, RefreshCcw, Clock3, AlertCircle, LogOut, User } from "lucide-react";

const BASE_URL = "http://localhost:8080/api/catalogue";

const CataloguePage = ({ userId, onLogout, onSelectItem }) => {
    const [items, setItems] = useState([]);
    const [isBusy, setIsBusy] = useState(true);
    const [failMsg, setFailMsg] = useState("");

    const [query, setQuery] = useState("");
    const [kind, setKind] = useState("all");
    const [costSpan, setCostSpan] = useState("all");

    // Fetch active auctions
    const loadActive = useCallback(async () => {
        try {
            const res = await fetch(`${BASE_URL}/active`);
            if (!res.ok) throw new Error("Could not retrieve auctions.");

            const body = await res.json();
            if (!Array.isArray(body)) return;

            setItems((prev) => {
                const map = new Map(prev.map((i) => [i.id, i]));

                body.forEach((fresh) => {
                    const old = map.get(fresh.id);

                    if (old) {
                        map.set(fresh.id, {
                            ...old,
                            currentBid: fresh.currentBid,
                            timeLeft: fresh.timeLeft,
                            closed: fresh.closed,
                            currentHighestBidderId: fresh.currentHighestBidderId
                        });
                    } else {
                        map.set(fresh.id, fresh);
                    }
                });

                return Array.from(map.values());
            });

        } catch (ex) {
            setFailMsg(ex.message ?? "Unexpected error.");
        }
    }, []);


    // Auto-refresh active auctions every 1 second
    useEffect(() => {
        const firstLoad = async () => {
            try {
                await loadActive();
            } finally {
                setIsBusy(false); // Only ONCE
            }
        };

        firstLoad();

        const interval = setInterval(() => {
            loadActive();      // silent updates
        }, 100);

        return () => clearInterval(interval);
    }, []);  // YES, EMPTY ARRAY



    // Utilities
    const money = (val) => {
        const n = Number(val);
        if (Number.isNaN(n)) return "$0.00";
        return n.toLocaleString(undefined, {
            style: "currency",
            currency: "USD",
            minimumFractionDigits: 2,
        });
    };

    // Extract unique item types
    const typeList = useMemo(() => {
        const s = new Set();
        items.forEach((i) => i.type && s.add(i.type));
        return ["all", ...Array.from(s)];
    }, [items]);

    // Filtering logic
    const visibleItems = useMemo(() => {
        const q = query.toLowerCase().trim();

        return items
            .filter((i) => i.name?.toLowerCase().includes(q))
            .filter((i) => (kind === "all" ? true : i.type === kind))
            .filter((i) => {
                const current = Number(i.currentBid || 0);

                switch (costSpan) {
                    case "under50":
                        return current < 50;
                    case "50to200":
                        return current >= 50 && current <= 200;
                    case "over200":
                        return current > 200;
                    default:
                        return true;
                }
            });
    }, [items, query, kind, costSpan]);

    return (
        <div className="min-h-screen bg-slate-50">
            {/* HEADER */}
            <header className="bg-gradient-to-r from-blue-600 to-indigo-600 text-white py-8 shadow-lg">
                <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
                    {/* Title + Refresh */}
                    <div className="flex flex-col sm:flex-row sm:justify-between sm:items-center gap-4">
                        <div>
                            <p className="text-sm tracking-widest uppercase text-blue-100 mb-1">
                                Ongoing Items
                            </p>
                            <h1 className="text-3xl sm:text-4xl font-bold">Catalogue</h1>
                            <p className="text-blue-100 mt-2">
                                Search through active listings and refine using filters.
                            </p>
                            {userId && (
                                <p className="text-blue-100 text-sm mt-1 flex items-center gap-2">
                                    <User size={14} /> Logged in as User ID: {userId}
                                </p>
                            )}
                        </div>

                        <div className="flex gap-2">
                            {userId && onLogout && (
                                <button
                                    onClick={onLogout}
                                    className="inline-flex gap-2 items-center px-4 py-2 rounded-lg bg-red-500 border border-red-600 hover:bg-red-600 transition text-white"
                                >
                                    <LogOut size={16} />
                                    Sign Out
                                </button>
                            )}
                            <button
                                onClick={loadActive}
                                className="inline-flex gap-2 items-center px-4 py-2 rounded-lg bg-white/20 border border-white/30 hover:bg-white/30 transition"
                            >
                                <RefreshCcw size={16} />
                                Reload
                            </button>
                        </div>
                    </div>

                    {/* Search + Filters */}
                    <div className="mt-6 grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
                        {/* Search */}
                        <div className="flex items-center gap-2 bg-white/20 rounded-lg px-4 py-3">
                            <Search size={18} className="text-white" />
                            <input
                                type="text"
                                placeholder="Search items"
                                value={query}
                                onChange={(e) => setQuery(e.target.value)}
                                className="w-full bg-transparent text-white placeholder:text-blue-100 focus:outline-none"
                            />
                        </div>

                        {/* Type Filter */}
                        <div className="flex items-center gap-3 bg-white/10 rounded-lg px-4 py-3">
                            <Filter size={18} className="text-white" />
                            <select
                                value={kind}
                                onChange={(e) => setKind(e.target.value)}
                                className="w-full bg-transparent text-white focus:outline-none"
                            >
                                {typeList.map((t) => (
                                    <option key={t} value={t} className="text-slate-900">
                                        {t === "all" ? "All categories" : t}
                                    </option>
                                ))}
                            </select>
                        </div>

                        {/* Price Filter */}
                        <div className="flex items-center gap-3 bg-white/10 rounded-lg px-4 py-3">
                            <Tag size={18} className="text-white" />
                            <select
                                value={costSpan}
                                onChange={(e) => setCostSpan(e.target.value)}
                                className="w-full bg-transparent text-white focus:outline-none"
                            >
                                <option className="text-slate-900" value="all">
                                    All prices
                                </option>
                                <option className="text-slate-900" value="under50">
                                    Under $50
                                </option>
                                <option className="text-slate-900" value="50to200">
                                    $50 - $200
                                </option>
                                <option className="text-slate-900" value="over200">
                                    Above $200
                                </option>
                            </select>
                        </div>
                    </div>
                </div>
            </header>

            {/* BODY */}
            <main className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
                {/* Error banner */}
                {failMsg && (
                    <div className="flex items-center gap-3 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-6">
                        <AlertCircle size={18} />
                        <div>
                            <p className="font-semibold">Failed to load items.</p>
                            <p className="text-sm">{failMsg}</p>
                        </div>
                    </div>
                )}

                {/* Loading Skeleton */}
                {isBusy ? (
                    <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-6">
                        {Array.from({ length: 6 }).map((_, i) => (
                            <div
                                key={i}
                                className="h-48 bg-white border border-slate-200 rounded-xl shadow-sm animate-pulse"
                            />
                        ))}
                    </div>
                ) : visibleItems.length === 0 ? (
                    /* Empty state */
                    <div className="bg-white border border-dashed border-slate-300 rounded-xl p-10 text-center shadow-sm">
                        <div className="w-12 h-12 mx-auto mb-4 rounded-full bg-slate-100 flex justify-center items-center">
                            <Search size={20} className="text-slate-500" />
                        </div>
                        <h2 className="text-xl font-semibold text-slate-800 mb-2">
                            Nothing matches your search
                        </h2>
                        <p className="text-slate-500 max-w-lg mx-auto">
                            Try adjusting filters or refreshing to see the latest active items.
                        </p>
                    </div>
                ) : (
                    /* Cards */
                    <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-6">
                        {visibleItems.map((a) => (
                            <article
                                key={a.id}
                                className="bg-white border border-slate-200 rounded-xl shadow-sm hover:shadow-md transition overflow-hidden cursor-pointer"
                                onClick={() => onSelectItem(a)}
                            >
                                {/* IMAGE */}
                                {a.imageUrl && (
                                    <img
                                        src={a.imageUrl}
                                        alt={a.name}
                                        className="w-full h-40 object-cover border-b border-slate-200"
                                    />
                                )}

                                {/* BODY CONTENT */}
                                <div className="p-4 flex flex-col gap-3">
                                    <div className="flex justify-between items-start">
                                        <div>
                                            <p className="text-xs uppercase tracking-wider text-slate-500">
                                                Item #{a.id}
                                            </p>
                                            <h3 className="text-lg font-semibold text-slate-900 leading-tight">
                                                {a.name}
                                            </h3>
                                        </div>
                                        {a.type && (
                                            <span
                                                className="px-3 py-1 text-xs rounded-full bg-blue-50 text-blue-700 border border-blue-100">
                    {a.type}
                </span>
                                        )}
                                    </div>

                                    <div className="flex justify-between bg-slate-50 rounded-lg px-3 py-2 items-center">
                                        <div>
                                            <p className="text-xs text-slate-500">Current bid</p>
                                            <p className="text-xl font-bold">{money(a.currentBid)}</p>
                                        </div>
                                        <div
                                            className="flex items-center gap-2 px-3 py-1 rounded-full bg-amber-50 text-amber-700 text-sm">
                                            <Clock3 size={16}/>
                                            <span>{a.timeLeft || "—"}</span>
                                        </div>
                                    </div>

                                    <p className="text-sm text-slate-600">
                                        Competitive auction currently at {money(a.currentBid)}. Place your
                                        offer before time runs out.
                                    </p>
                                </div>
                            </article>
                        ))}
                    </div>
                )}
            </main>
        </div>
    );
};

export default CataloguePage;