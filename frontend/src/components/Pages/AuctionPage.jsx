import React, { useState, useEffect } from "react";
import { RefreshCw, Trophy, CreditCard, LogOut, User } from "lucide-react";
import "../../styles/AuctionStyle.css"; // Ensure this path matches your folder structure

const AuctionPage = ({
  currentUserId,
  token,
  onRequestLogin,
  onBuyNow,
  onLogout,
}) => {
  const [auctions, setAuctions] = useState([]);
  const [loading, setLoading] = useState(true);

  const [bidAmount, setBidAmount] = useState("");
  const [selectedAuctionId, setSelectedAuctionId] = useState(null);
  const [message, setMessage] = useState({ type: "", text: "" });

  // STATE FOR THE WINNER POPUP
  const [winningItem, setWinningItem] = useState(null);

  useEffect(() => {
    fetchAuctions();
    // Auto-refresh every 5 seconds to keep data synced
    const interval = setInterval(fetchAuctions, 5000);
    return () => clearInterval(interval);
  }, []);

  const fetchAuctions = async () => {
    try {
      const response = await fetch(
        "http://localhost:8080/api/catalogue/active"
      );
      if (!response.ok) throw new Error("Failed to fetch");
      const data = await response.json();

      // Sort: Open items first, then by ID
      const sortedData = data.sort((a, b) =>
        a.closed === b.closed ? b.id - a.id : a.closed ? 1 : -1
      );
      setAuctions(sortedData);
      setLoading(false);
    } catch (error) {
      console.error("Error:", error);
      setLoading(false);
    }
  };

  const handlePlaceBid = async (e, auction) => {
    e.preventDefault();
    setMessage({ type: "", text: "" });
    console.log(bidAmount, auction);
    // Handle data mismatch (backend might send 'type' or 'auctionType')
    const type = auction.type || auction.auctionType;
    const isDutch = type === "DUTCH";

    if (!isDutch && !bidAmount) return;

    const payload = {
      auctionId: auction.id,
      userId: parseInt(currentUserId),
      bidAmount: isDutch ? 0.0 : parseFloat(bidAmount),
    };

    console.log("Placing bid with payload:", payload);

    try {
      // 1. Check if user is logged in
      if (!token) {
        setMessage({ type: "error", text: "You must be logged in to bid!" });
        onRequestLogin();
        return;
      }

      const response = await fetch("http://localhost:8080/api/bids/place", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(payload),
      });

      console.log("response sent", response);

      if (response.ok) {
        if (isDutch) {
          setWinningItem(auction); // Open popup

          // ✅ CRITICAL FIX: Optimistically update local state.
          // This prevents the user from clicking "Buy Now" a second time
          // and immediately shows the "Pay Now" button logic.
          setAuctions((prev) =>
            prev.map((a) => {
              if (a.id === auction.id) {
                return {
                  ...a,
                  closed: true,
                  currentHighestBidderId: parseInt(currentUserId),
                  // Keep current price
                  currentHighestBid:
                    a.currentHighestBid || a.currentBid || a.startingPrice,
                };
              }
              return a;
            })
          );
        } else {
          setMessage({ type: "success", text: `Bid of $${bidAmount} placed!` });
          fetchAuctions(); // Refresh list for Forward auctions
        }
      } else {
        const errorText = await response.text();
        setMessage({ type: "error", text: `Rejected: ${errorText}` });
      }
    } catch (error) {
      setMessage({ type: "error", text: "Network error." });
    } finally {
      setBidAmount("");
      setSelectedAuctionId(null);
    }
  };

  const handlePaymentRedirect = () => {
    // Navigate to Purchase Page using the prop function
    if (onBuyNow) {
      onBuyNow(winningItem);
    }
  };

  // ✅ LOGIC FIX: Filter the list BEFORE rendering.
  // Rule: Show auction IF (It is Open) OR (It is Closed AND I am the Winner).
  // Everyone else (new users, losers) should NOT see closed auctions.
  const visibleAuctions = auctions.filter((a) => {
    // 1. If auction is OPEN, everyone sees it
    if (!a.closed) return true;

    // 2. If auction is CLOSED, only the WINNER sees it (to pay)
    if (a.closed && a.currentHighestBidderId === parseInt(currentUserId))
      return true;

    // 3. Otherwise (Closed + Not Winner), hide it.
    return false;
  });

  return (
    <div className="app-container">
      <div className="card">
        <div
          className="card-header"
          style={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
          }}
        >
          <div>
            <h1 style={{ margin: 0 }}>Auction404</h1>
            <p style={{ margin: 0, fontSize: "14px", opacity: 0.9 }}>
              Live Marketplace
            </p>
            {!currentUserId && (
              <span
                style={{
                  fontSize: "10px",
                  background: "rgba(255,255,255,0.2)",
                  padding: "2px 6px",
                  borderRadius: "4px",
                }}
              >
                Guest View
              </span>
            )}
            {currentUserId && (
              <span
                style={{
                  fontSize: "11px",
                  background: "rgba(255,255,255,0.2)",
                  padding: "2px 8px",
                  borderRadius: "4px",
                  display: "inline-flex",
                  alignItems: "center",
                  gap: "4px",
                  marginTop: "4px",
                }}
              >
                <User size={12} /> User ID: {currentUserId}
              </span>
            )}
          </div>
          <div style={{ display: "flex", gap: "10px", alignItems: "center" }}>
            {currentUserId && onLogout && (
              <button
                onClick={onLogout}
                style={{
                  display: "flex",
                  alignItems: "center",
                  gap: "6px",
                  padding: "8px 14px",
                  background: "#ef4444",
                  color: "white",
                  border: "none",
                  borderRadius: "6px",
                  cursor: "pointer",
                  fontSize: "13px",
                  fontWeight: "500",
                }}
              >
                <LogOut size={14} />
                Sign Out
              </button>
            )}
            <button
              className="btn-refresh"
              onClick={fetchAuctions}
              title="Refresh Prices"
            >
              <RefreshCw size={20} />
            </button>
          </div>
        </div>

        <div className="card-body">
          {message.text && (
            <div
              className={`alert ${
                message.type === "error" ? "error-msg" : "success-msg"
              }`}
              style={{ marginBottom: "15px" }}
            >
              {message.text}
            </div>
          )}

          {loading ? (
            <p style={{ textAlign: "center" }}>Loading auctions...</p>
          ) : (
            // Added scroll styles here
            <div
              className="auction-list"
              style={{
                maxHeight: "75vh",
                overflowY: "auto",
                paddingRight: "5px",
              }}
            >
              {visibleAuctions.map((auction) => {
                // Check if I won this specific auction
                const iWon =
                  auction.closed &&
                  auction.currentHighestBidderId === parseInt(currentUserId);

                // Handle naming mismatch from backend
                const title = auction.itemName || auction.title || auction.name;
                const type = auction.auctionType || auction.type || "FORWARD";
                const price =
                  auction.currentHighestBid ||
                  auction.currentBid ||
                  auction.startingPrice;

                return (
                  <div
                    key={auction.id}
                    className="auction-row"
                    style={{
                      opacity: auction.closed && !iWon ? 0.6 : 1,
                      border: iWon ? "2px solid #10b981" : "1px solid #eee",
                    }}
                  >
                    <div className="item-info">
                      <h3>
                        {title}
                        <span
                          className={`badge ${
                            type === "DUTCH" ? "badge-dutch" : "badge-forward"
                          }`}
                        >
                          {type}
                        </span>
                        {/* Show Winner Badge */}
                        {iWon && (
                          <span
                            style={{
                              marginLeft: "10px",
                              color: "#10b981",
                              fontWeight: "bold",
                            }}
                          >
                            🏆 YOU WON
                          </span>
                        )}
                      </h3>

                      {/* Time Logic */}
                      {auction.closed ? (
                        <p
                          style={{
                            color: iWon ? "#10b981" : "red",
                            fontWeight: "bold",
                          }}
                        >
                          {iWon ? "READY FOR PAYMENT" : "SOLD"}
                        </p>
                      ) : (
                        (() => {
                          // Safe date parsing
                          const endTime = new Date(
                            auction.endTime || Date.now()
                          );
                          const now = new Date();
                          const diffMs = endTime - now;

                          if (diffMs <= 0)
                            return (
                              <p style={{ color: "red", fontWeight: "bold" }}>
                                Time Expired
                              </p>
                            );

                          // Calculate time units
                          const days = Math.floor(
                            diffMs / (1000 * 60 * 60 * 24)
                          );
                          const hours = Math.floor(
                            (diffMs % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
                          );
                          const minutes = Math.floor(
                            (diffMs % (1000 * 60 * 60)) / (1000 * 60)
                          );

                          // Construct string dynamically
                          let timeString = "";
                          if (days > 0) timeString += `${days}d `;
                          if (hours > 0 || days > 0) timeString += `${hours}h `;
                          timeString += `${minutes}m`;

                          return <p>Ends in: {timeString}</p>;
                        })()
                      )}
                    </div>

                    {currentUserId ? (
                      /* ✅ CASE 1: I WON (Closed + My ID) -> Show Pay Button */
                      iWon ? (
                        <button
                          className="btn-buy-now"
                          onClick={() => setWinningItem(auction)}
                        >
                          Pay Now
                        </button>
                      ) : /* CASE 2: DUTCH (Open) -> Show Buy Now */
                      type === "DUTCH" ? (
                        <button
                          className="btn-buy-now"
                          onClick={(e) => handlePlaceBid(e, auction)}
                          disabled={auction.closed} // Disabled if closed
                        >
                          {auction.closed ? "Sold" : "Buy Now"}
                        </button>
                      ) : (
                        /* CASE 3: FORWARD (Open) -> Show Bid Form */
                        <form
                          onSubmit={(e) => handlePlaceBid(e, auction)}
                          style={{ display: "flex", gap: "10px" }}
                        >
                          <input
                            type="number"
                            step="0.01"
                            className="bid-input"
                            placeholder="Bid.."
                            value={
                              selectedAuctionId === auction.id ? bidAmount : ""
                            }
                            onChange={(e) => {
                              setSelectedAuctionId(auction.id);
                              setBidAmount(e.target.value);
                            }}
                            disabled={auction.closed}
                          />
                          <button
                            type="submit"
                            className="btn-bid"
                            disabled={auction.closed}
                          >
                            Bid
                          </button>
                        </form>
                      )
                    ) : (
                      <button
                        className="btn-bid"
                        style={{ background: "#666" }}
                        onClick={onRequestLogin}
                      >
                        Sign In
                      </button>
                    )}
                  </div>
                );
              })}
            </div>
          )}
        </div>
      </div>

      {/* ✅ WINNER POPUP MODAL */}
      {winningItem && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div className="modal-icon">🎉</div>
            <h2 className="modal-title">Hooray!</h2>
            <p>
              You won the{" "}
              <strong>
                {winningItem.itemName || winningItem.title || winningItem.name}
              </strong>
              !
            </p>

            <div className="modal-price">
              Pay: $
              {winningItem.currentHighestBid ||
                winningItem.currentBid ||
                winningItem.startingPrice}
            </div>

            <button className="btn-pay" onClick={handlePaymentRedirect}>
              <CreditCard
                size={18}
                style={{ marginRight: "8px", verticalAlign: "text-bottom" }}
              />
              Proceed to Payment
            </button>

            <button
              className="btn-close-modal"
              onClick={() => setWinningItem(null)}
            >
              I'll pay later
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default AuctionPage;
