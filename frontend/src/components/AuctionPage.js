import React, { useState, useEffect } from 'react';
import { RefreshCw, Trophy, CreditCard } from 'lucide-react'; 
import '../styles/AuctionStyle.css';

const AuctionPage = ({ currentUserId, onRequestLogin }) => {
  const [auctions, setAuctions] = useState([]);
  const [loading, setLoading] = useState(true);
  
  const [bidAmount, setBidAmount] = useState('');
  const [selectedAuctionId, setSelectedAuctionId] = useState(null);
  const [message, setMessage] = useState({ type: '', text: '' });

  // ✅ STATE FOR THE WINNER POPUP
  const [winningItem, setWinningItem] = useState(null); 

  useEffect(() => {
    fetchAuctions();
  }, []);

  const fetchAuctions = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/auctions');
      if (!response.ok) throw new Error('Failed to fetch');
      const data = await response.json();
      const sortedData = data.sort((a, b) => (a.closed === b.closed) ? b.id - a.id : a.closed ? 1 : -1);
      setAuctions(sortedData);
      setLoading(false);
    } catch (error) {
      console.error("Error:", error);
      setLoading(false);
    }
  };

  const handlePlaceBid = async (e, auction) => {
    e.preventDefault();
    setMessage({ type: '', text: '' });

    const isDutch = auction.auctionType === 'DUTCH';

    if (!isDutch && !bidAmount) return;

    const payload = {
      auctionId: auction.id,
      userId: parseInt(currentUserId),
      bidAmount: isDutch ? 0.00 : parseFloat(bidAmount)
    };

    try {
      const response = await fetch('http://localhost:8080/api/bids/place', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      });

      if (response.ok) {
        // ✅ LOGIC UPDATE: If it's Dutch, they won instantly! Show Popup.
        if (isDutch) {
            setWinningItem(auction); // Open the popup
        } else {
            setMessage({ type: 'success', text: `Bid of $${bidAmount} placed!` });
        }
        fetchAuctions(); 
      } else {
        const errorText = await response.text(); 
        setMessage({ type: 'error', text: `Rejected: ${errorText}` });
      }
    } catch (error) {
      setMessage({ type: 'error', text: 'Network error.' });
    } finally {
      setBidAmount('');
      setSelectedAuctionId(null); 
    }
  };

  const handlePaymentRedirect = () => {
    alert("Redirecting to Secure Payment Gateway... (Feature coming soon)");
    setWinningItem(null); // Close modal
  };

  return (
    <div className="app-container">
      <div className="card">
        <div className="card-header" style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
          <div>
            <h1 style={{margin:0}}>Auction404</h1>
            <p style={{margin:0, fontSize:'14px', opacity:0.9}}>Live Marketplace</p>
            {!currentUserId && <span style={{fontSize:'10px', background:'rgba(255,255,255,0.2)', padding:'2px 6px', borderRadius:'4px'}}>Guest View</span>}
          </div>
          <button className="btn-refresh" onClick={fetchAuctions} title="Refresh Prices">
             <RefreshCw size={20} />
          </button>
        </div>

        <div className="card-body">
          {message.text && (
            <div className={`alert ${message.type === 'error' ? 'error-msg' : 'success-msg'}`} style={{marginBottom: '15px'}}>
              {message.text}
            </div>
          )}

          {loading ? (
            <p style={{textAlign: 'center'}}>Loading auctions...</p>
          ) : (
            <div className="auction-list">
              {auctions.map((auction) => {
                // Check if I won this specific auction
                const iWon = auction.closed && auction.currentHighestBidder && auction.currentHighestBidder.id === parseInt(currentUserId);
                
                return (
                <div key={auction.id} className="auction-row" style={{opacity: auction.closed && !iWon ? 0.6 : 1, border: iWon ? '2px solid #10b981' : '1px solid #eee'}}>
                  
                  <div className="item-info">
                    <h3>
                        {auction.itemName}
                        <span className={`badge ${auction.auctionType === 'DUTCH' ? 'badge-dutch' : 'badge-forward'}`}>
                            {auction.auctionType || 'FORWARD'}
                        </span>
                        {/* Show Winner Badge */}
                        {iWon && <span style={{marginLeft:'10px', color:'#10b981', fontWeight:'bold'}}>🏆 YOU WON</span>}
                    </h3>
                    
                    {auction.closed ? (
                        <p style={{color: 'red', fontWeight: 'bold'}}>SOLD / CLOSED</p>
                    ) : (
                        (() => {
                            const endTime = new Date(auction.endTime);
                            const now = new Date();
                            const diffMs = endTime - now;
                            const diffMins = Math.floor(diffMs / 60000);
                            if (diffMins <= 0) return <p style={{color: 'red', fontWeight: 'bold'}}>Time Expired</p>;
                            return <p>Ends in: {diffMins} mins</p>;
                        })()
                    )}
                  </div>

                  <div className="item-actions">
                    <div className="current-price">
                      ${auction.currentHighestBid || auction.startingPrice}
                    </div>
                    
                    {currentUserId ? (
                        /* ✅ CASE 1: I WON (Closed) -> Show Pay Button */
                        iWon ? (
                            <button className="btn-buy-now" onClick={() => setWinningItem(auction)}>
                                Pay Now
                            </button>
                        ) :
                        /* CASE 2: DUTCH (Open) -> Show Buy Now */
                        auction.auctionType === 'DUTCH' ? (
                            <button 
                                className="btn-buy-now"
                                onClick={(e) => handlePlaceBid(e, auction)}
                                disabled={auction.closed}
                            >
                                {auction.closed ? 'Sold' : 'Buy Now'}
                            </button>
                        ) : (
                            /* CASE 3: FORWARD (Open) -> Show Bid Form */
                            <form onSubmit={(e) => handlePlaceBid(e, auction)} style={{display: 'flex', gap: '10px'}}>
                              <input
                                type="number"
                                step="0.01"
                                className="bid-input"
                                placeholder="Bid.."
                                value={selectedAuctionId === auction.id ? bidAmount : ''}
                                onChange={(e) => {
                                  setSelectedAuctionId(auction.id);
                                  setBidAmount(e.target.value);
                                }}
                                disabled={auction.closed}
                              />
                              <button type="submit" className="btn-bid" disabled={auction.closed}>Bid</button>
                            </form>
                        )
                    ) : (
                        <button className="btn-bid" style={{background: '#666'}} onClick={onRequestLogin}>Sign In</button>
                    )}
                  </div>
                </div>
              )})}
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
                <p>You won the <strong>{winningItem.itemName}</strong>!</p>
                
                <div className="modal-price">
                    Pay: ${winningItem.currentHighestBid}
                </div>

                <button className="btn-pay" onClick={handlePaymentRedirect}>
                    <CreditCard size={18} style={{marginRight:'8px', verticalAlign:'text-bottom'}}/>
                    Proceed to Payment
                </button>

                <button className="btn-close-modal" onClick={() => setWinningItem(null)}>
                    I'll pay later
                </button>
            </div>
        </div>
      )}

    </div>
  );
};

export default AuctionPage;