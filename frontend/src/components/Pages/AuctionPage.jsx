import React, { useState, useEffect } from "react";
import { RefreshCw, Trophy, CreditCard } from "lucide-react";
import "../../styles/AuctionStyle.css";

// ✅ PROPS: Added 'onBuyNow' to connect to App.js
const AuctionPage = ({ currentUserId, token, onRequestLogin, onBuyNow }) => {
  const [auctions, setAuctions] = useState([]);
  const [loading, setLoading] = useState(true);

  const [bidAmount, setBidAmount] = useState("");
  const [selectedAuctionId, setSelectedAuctionId] = useState(null);
  const [message, setMessage] = useState({ type: "", text: "" });

  // ✅ STATE FOR THE WINNER POPUP
  const [winningItem, setWinningItem] = useState(null);

  useEffect(() => {
    fetchAuctions();
    // Optional: Auto-refresh every 5 seconds to see live bids
    const interval = setInterval(fetchAuctions, 5000);
    return () => clearInterval(interval);
  }, []);

  const fetchAuctions = async () => {
    try {
      // ✅ FIX: Using the endpoint we confirmed works
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
        // ✅ LOGIC UPDATE: If it's Dutch, they won instantly! Show Popup.
        if (isDutch) {
          setWinningItem(auction); // Open the popup
        } else {
          setMessage({ type: "success", text: `Bid of $${bidAmount} placed!` });
        }
        fetchAuctions();
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
    // ✅ FIX: Navigate to Purchase Page
    if (onBuyNow) {
      onBuyNow(winningItem);
    }
  };

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
          </div>
          <button
            className="btn-refresh"
            onClick={fetchAuctions}
            title="Refresh Prices"
          >
            <RefreshCw size={20} />
          </button>
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
            // ✅ SCROLL FIX: Added styles here to enable scrolling
            <div
              className="auction-list"
              style={{
                maxHeight: "75vh",
                overflowY: "auto",
                paddingRight: "5px",
              }}
            >
              {auctions.map((auction) => {
                // Check if I won this specific auction
                const iWon =
                  auction.closed &&
                  auction.currentHighestBidderId === parseInt(currentUserId);

                // Handle naming mismatch
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

                      {auction.closed ? (
                        <p style={{ color: "red", fontWeight: "bold" }}>
                          SOLD / CLOSED
                        </p>
                      ) : (
                        (() => {
                          // Safe date parsing
                          const endTime = new Date(auction.endTime);
                          const now = new Date();
                          const diffMs = endTime - now;

                          // new timeLeft check
                          const timeLeft = auction.timeLeft || diffMs;
                          // Check if time is up
                          if (timeLeft <= 0)
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

                          // Construct string dynamically (e.g., "1d 2h 30m" or just "2h 30m")
                          let timeString = "";
                          if (days > 0) timeString += `${days}d `;
                          if (hours > 0 || days > 0) timeString += `${hours}h `; // Show 0h if we have days
                          timeString += `${minutes}m`;

                          return <p>Ends in: {timeLeft}</p>;
                        })()
                      )}
                    </div>

                    <div className="item-actions">
                      <div className="current-price">${price}</div>

                      {currentUserId ? (
                        /* ✅ CASE 1: I WON (Closed) -> Show Pay Button */
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
                            disabled={auction.closed}
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
                                selectedAuctionId === auction.id
                                  ? bidAmount
                                  : ""
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
