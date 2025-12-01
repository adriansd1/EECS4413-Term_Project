import React, { useState, useEffect } from "react";
import {
  RefreshCw,
  ArrowLeft,
  CreditCard,
  Clock,
  Gavel,
  ShoppingCart,
  AlertCircle
} from "lucide-react";
import "../../styles/AuctionStyle.css";

const AuctionPage = ({
  item,
  currentUserId,
  token,
  onRequestLogin,
  onBack,
  onBuyNow,
}) => {
  // Use local state to track price updates for this specific item
  const [auction, setAuction] = useState(item);
  
  // Bid Form State
  const [bidOption, setBidOption] = useState("custom"); // 'opt1', 'opt2', 'opt3', 'custom'
  const [customBidAmount, setCustomBidAmount] = useState("");
  
  const [message, setMessage] = useState({ type: "", text: "" });
  const [winningItem, setWinningItem] = useState(null);

  // Refresh this item to see new bids
  useEffect(() => {
    const fetchLatest = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/catalogue/active');
            const data = await response.json();
            const updated = data.find(a => a.id === item.id);
            if (updated) setAuction(updated);
        } catch (err) {
            console.error(err);
        }
    };
    // Initial fetch + Interval
    fetchLatest();
    const interval = setInterval(fetchLatest, 3000); // Poll every 3s
    return () => clearInterval(interval);
  }, [item]);

  const title = auction?.title || auction?.name || "Unknown Item";
  const price =
    auction?.currentHighestBid ||
    auction?.currentBid ||
    auction?.startingPrice ||
    0;
  const isDutch = auction?.type === "DUTCH" || auction?.auctionType === "DUTCH";

  // Check if I won
  const iWon =
    auction?.closed &&
    auction?.currentHighestBidderId === parseInt(currentUserId);

  // --- DYNAMIC BID OPTIONS ---
  // Calculate sensible quick-bid increments based on price
  const inc1 = 10;
  const inc2 = 50;
  const inc3 = 100;
  
  const opt1Value = price + inc1;
  const opt2Value = price + inc2;
  const opt3Value = price + inc3;

  const handlePlaceBid = async (e) => {
    e.preventDefault();
    setMessage({ type: "", text: "" });

    if (!token) {
      setMessage({ type: "error", text: "You must be logged in to bid!" });
      onRequestLogin();
      return;
    }

    // --- VALIDATION LOGIC ---
    let finalAmount = 0;

    if (isDutch) {
        finalAmount = 0.0; // Buy Now
    } else {
        // Determine amount based on radio selection
        if (bidOption === 'opt1') finalAmount = opt1Value;
        else if (bidOption === 'opt2') finalAmount = opt2Value;
        else if (bidOption === 'opt3') finalAmount = opt3Value;
        else finalAmount = parseFloat(customBidAmount);

        // 1. Check for valid number
        if (isNaN(finalAmount)) {
             setMessage({ type: "error", text: "Please enter a valid number." });
             return;
        }

        // 2. Check for Negative
        if (finalAmount <= 0) {
            setMessage({ type: "error", text: "Bid amount must be positive." });
            return;
        }

        // 3. Check against Current Price
        if (finalAmount <= price) {
            setMessage({ type: "error", text: `Bid must be higher than current price ($${price}).` });
            return;
        }
    }

    const payload = {
      auctionId: auction.id,
      userId: parseInt(currentUserId),
      bidAmount: finalAmount,
    };

    try {
      const response = await fetch("http://localhost:8080/api/bids/place", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(payload),
      });

      if (response.ok) {
        if (isDutch) {
          setWinningItem(auction); // Open popup immediately
        } else {
          setMessage({ type: "success", text: `Bid of $${finalAmount} placed!` });
          setCustomBidAmount("");
          setBidOption("custom"); // Reset form
        }
      } else {
        const errorText = await response.text();
        setMessage({ type: "error", text: `Rejected: ${errorText}` });
      }
    } catch (error) {
      setMessage({ type: "error", text: "Network error." });
    }
  };

  const handlePaymentRedirect = () => {
    if (onBuyNow) onBuyNow(winningItem);
  };

  if (!auction) return <div style={{ padding: "20px" }}>Loading Item...</div>;

  return (
    <div className="app-container">
      <div className="card">
        {/* HEADER */}
        <div
          className="card-header"
          style={{ display: "flex", gap: "15px", alignItems: "center" }}
        >
          <button
            onClick={onBack}
            style={{
              background: "none",
              border: "none",
              color: "white",
              cursor: "pointer",
              display: "flex",
              alignItems: "center",
              gap: "5px",
            }}
          >
            <ArrowLeft size={20} /> Back to Catalogue
          </button>
        </div>

        <div className="card-body">
          {message.text && (
            <div
              className={`alert ${
                message.type === "error" ? "error-msg" : "success-msg"
              }`}
              style={{ marginBottom: "15px", display:'flex', alignItems:'center', gap:'10px' }}
            >
              {message.type === "error" && <AlertCircle size={18}/>}
              {message.text}
            </div>
          )}

          {/* ITEM DETAIL VIEW */}
          <div style={{ textAlign: "center", padding: "20px" }}>
            <h2 style={{ fontSize: "2rem", marginBottom: "10px" }}>{title}</h2>

            <span
              className={`badge ${isDutch ? "badge-dutch" : "badge-forward"}`}
              style={{
                fontSize: "1rem",
                padding: "5px 15px",
                marginBottom: "15px",
                display: "inline-block",
              }}
            >
              {auction.type || auction.auctionType}
            </span>

            <p style={{ color: "#666", marginBottom: "30px", fontSize: "1.1rem" }}>
              {auction.description}
            </p>

            {/* PRICE BOX */}
            <div
              style={{
                background: "#f8f9fa",
                padding: "30px",
                borderRadius: "15px",
                display: "inline-block",
                minWidth: "350px",
                boxShadow: "0 4px 6px rgba(0,0,0,0.05)",
                textAlign: 'left'
              }}
            >
              {auction.closed ? (
                <div style={{textAlign:'center'}}>
                  <h3
                    style={{
                      color: iWon ? "#10b981" : "#ef4444",
                      fontSize: "1.5rem",
                      fontWeight: "bold",
                      marginBottom: "10px",
                    }}
                  >
                    {iWon ? "🎉 YOU WON!" : "SOLD"}
                  </h3>
                  <div
                    style={{
                      fontSize: "2.5rem",
                      fontWeight: "bold",
                      color: "#333",
                      marginBottom: "15px",
                    }}
                  >
                    ${price.toLocaleString()}
                  </div>
                  {iWon && (
                    <button
                      className="btn-pay"
                      onClick={() => setWinningItem(auction)}
                      style={{ width: "100%" }}
                    >
                      Complete Payment
                    </button>
                  )}
                </div>
              ) : (
                <>
                  <div style={{textAlign:'center', marginBottom:'20px'}}>
                      <span style={{ fontSize: "0.9rem", color: "#888", textTransform: "uppercase", letterSpacing: "1px" }}>
                        Current Price
                      </span>
                      <div style={{ fontSize: "3.5rem", fontWeight: "bold", color: "#2563eb", margin: "5px 0" }}>
                        ${price.toLocaleString()}
                      </div>
                      <div style={{ display: "flex", justifyContent: "center", gap: "5px", color: "#d97706", fontWeight: "500" }}>
                        <Clock size={20} /> <span>Ends in: {auction.timeLeft || "Active"}</span>
                      </div>
                  </div>

                  {/* BIDDING CONTROLS */}
                  <div style={{ marginTop: "20px", borderTop:'1px solid #ddd', paddingTop:'20px' }}>
                    {isDutch ? (
                      <button
                        className="btn-buy-now"
                        style={{ width: "100%", padding: "15px", fontSize: "1.2rem" }}
                        onClick={handlePlaceBid}
                      >
                        <ShoppingCart size={20} style={{ verticalAlign: "middle", marginRight: "8px" }} />
                        BUY NOW
                      </button>
                    ) : (
                      <form onSubmit={handlePlaceBid}>
                        <p style={{fontWeight:'bold', marginBottom:'10px', color:'#555'}}>Select Bid Amount:</p>
                        
                        {/* RADIO BUTTONS */}
                        <div style={{display:'flex', flexDirection:'column', gap:'10px', marginBottom:'15px'}}>
                            
                            {/* Option 1 */}
                            <label className={`bid-radio ${bidOption === 'opt1' ? 'selected' : ''}`}>
                                <input type="radio" name="bidOption" value="opt1" checked={bidOption === 'opt1'} onChange={() => setBidOption('opt1')} />
                                <span>Bid <strong>${opt1Value.toLocaleString()}</strong> (+${inc1})</span>
                            </label>

                            {/* Option 2 */}
                            <label className={`bid-radio ${bidOption === 'opt2' ? 'selected' : ''}`}>
                                <input type="radio" name="bidOption" value="opt2" checked={bidOption === 'opt2'} onChange={() => setBidOption('opt2')} />
                                <span>Bid <strong>${opt2Value.toLocaleString()}</strong> (+${inc2})</span>
                            </label>

                            {/* Option 3 */}
                            <label className={`bid-radio ${bidOption === 'opt3' ? 'selected' : ''}`}>
                                <input type="radio" name="bidOption" value="opt3" checked={bidOption === 'opt3'} onChange={() => setBidOption('opt3')} />
                                <span>Bid <strong>${opt3Value.toLocaleString()}</strong> (+${inc3})</span>
                            </label>

                            {/* Custom Option */}
                            <label className={`bid-radio ${bidOption === 'custom' ? 'selected' : ''}`}>
                                <input type="radio" name="bidOption" value="custom" checked={bidOption === 'custom'} onChange={() => setBidOption('custom')} />
                                <span>Custom Amount</span>
                            </label>
                        </div>

                        {/* CUSTOM INPUT (Only visible if Custom selected) */}
                        {bidOption === 'custom' && (
                            <div style={{marginBottom:'15px'}}>
                                <input
                                    type="number"
                                    step="0.01"
                                    min={price + 0.01} // HTML Validation
                                    className="bid-input"
                                    style={{ padding: "10px", fontSize: "1rem", width: "100%" }}
                                    placeholder={`Enter amount > $${price}`}
                                    value={customBidAmount}
                                    onChange={(e) => setCustomBidAmount(e.target.value)}
                                />
                            </div>
                        )}

                        <button
                          type="submit"
                          className="btn-bid"
                          style={{ width: "100%", padding: "12px", fontSize:'1.1rem' }}
                        >
                          <Gavel size={18} style={{marginRight:'5px'}}/> Place Bid
                        </button>
                      </form>
                    )}
                  </div>
                </>
              )}
            </div>
          </div>
        </div>
      </div>

      {/* WINNER POPUP */}
      {winningItem && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div className="modal-icon">🎉</div>
            <h2 className="modal-title">Hooray!</h2>
            <p>
              You won the <strong>{title}</strong>!
            </p>
            <div className="modal-price">Pay: ${price}</div>
            <button className="btn-pay" onClick={handlePaymentRedirect}>
              <CreditCard size={18} style={{ marginRight: "8px" }} /> Proceed to
              Payment
            </button>
            <button
              className="btn-close-modal"
              onClick={() => setWinningItem(null)}
            >
              Close
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default AuctionPage;