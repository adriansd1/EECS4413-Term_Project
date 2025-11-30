import React, { useState, useEffect } from 'react';
import { RefreshCw, ArrowLeft, CreditCard, Clock, Gavel, ShoppingCart } from 'lucide-react'; 
import '../../styles/AuctionStyle.css';

const AuctionPage = ({ item, currentUserId, token, onRequestLogin, onBack, onBuyNow }) => {
  
  // Use local state to track price updates for this specific item
  const [auction, setAuction] = useState(item);
  const [bidAmount, setBidAmount] = useState('');
  const [message, setMessage] = useState({ type: '', text: '' });
  const [winningItem, setWinningItem] = useState(null); 

  // ✅ POLL: Refresh JUST this item every 3 seconds to see new bids
  useEffect(() => {
    const fetchLatest = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/catalogue/active');
            const data = await response.json();
            // Find OUR item in the list to get the latest price/status
            const updated = data.find(a => a.id === item.id);
            if (updated) setAuction(updated);
        } catch (err) {
            console.error(err);
        }
    };
    // Initial fetch + Interval
    fetchLatest();
    const interval = setInterval(fetchLatest, 30000);
    return () => clearInterval(interval);
  }, [item]);

  // Handle Logic
  const title = auction?.title || auction?.name || "Unknown Item";
  const price = auction?.currentHighestBid || auction?.currentBid || auction?.startingPrice || 0;
  const isDutch = auction?.type === 'DUTCH' || auction?.auctionType === 'DUTCH';
  
  // Check if I won
  const iWon = auction?.closed && auction?.currentHighestBidderId === parseInt(currentUserId);

  const handlePlaceBid = async (e) => {
    e.preventDefault();
    setMessage({ type: '', text: '' });

    if (!token) {
      setMessage({ type: 'error', text: 'You must be logged in to bid!' });
      onRequestLogin();
      return;
    }

    // Forward Logic Check
    if (!isDutch && (!bidAmount || parseFloat(bidAmount) <= price)) {
        setMessage({ type: 'error', text: 'Bid must be higher than current price.' });
        return;
    }

    const payload = {
      auctionId: auction.id,
      userId: parseInt(currentUserId),
      bidAmount: isDutch ? 0.00 : parseFloat(bidAmount)
    };

    try {
      const response = await fetch('http://localhost:8080/api/bids/place', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}`},
        body: JSON.stringify(payload),
      });

      if (response.ok) {
        if (isDutch) {
            setWinningItem(auction); // Open popup immediately
        } else {
            setMessage({ type: 'success', text: `Bid of $${bidAmount} placed!` });
            setBidAmount('');
        }
      } else {
        const errorText = await response.text(); 
        setMessage({ type: 'error', text: `Rejected: ${errorText}` });
      }
    } catch (error) {
      setMessage({ type: 'error', text: 'Network error.' });
    }
  };

  const handlePaymentRedirect = () => {
    if (onBuyNow) onBuyNow(winningItem || auction);
  };

  if (!auction) return <div style={{padding:'20px'}}>Loading Item...</div>;

  return (
    <div className="app-container">
      <div className="card">
        
        {/* HEADER */}
        <div className="card-header" style={{display: 'flex', gap:'15px', alignItems: 'center'}}>
          <button onClick={onBack} style={{background:'none', border:'none', color:'white', cursor:'pointer', display:'flex', alignItems:'center', gap:'5px'}}>
            <ArrowLeft size={20} /> Back to Catalogue
          </button>
        </div>

        <div className="card-body">
          {message.text && (
            <div className={`alert ${message.type === 'error' ? 'error-msg' : 'success-msg'}`} style={{marginBottom: '15px'}}>
              {message.text}
            </div>
          )}

          {/* ITEM DETAIL VIEW */}
          <div style={{textAlign: 'center', padding: '20px'}}>
             <h2 style={{fontSize: '2rem', marginBottom: '10px'}}>{title}</h2>
             
             <span className={`badge ${isDutch ? 'badge-dutch' : 'badge-forward'}`} style={{fontSize:'1rem', padding:'5px 15px', marginBottom:'15px', display:'inline-block'}}>
                {auction.type || auction.auctionType}
             </span>

             <p style={{color: '#666', marginBottom: '30px', fontSize:'1.1rem'}}>{auction.description}</p>
             
             {/* PRICE BOX */}
             <div style={{background: '#f8f9fa', padding: '30px', borderRadius: '15px', display: 'inline-block', minWidth: '350px', boxShadow: '0 4px 6px rgba(0,0,0,0.05)'}}>
                
                {auction.closed ? (
                    <div>
                        <h3 style={{color: iWon ? '#10b981' : '#ef4444', fontSize:'1.5rem', fontWeight:'bold', marginBottom:'10px'}}>
                            {iWon ? '🎉 YOU WON!' : 'SOLD'}
                        </h3>
                        <div style={{fontSize: '2.5rem', fontWeight: 'bold', color: '#333', marginBottom:'15px'}}>
                            ${price.toLocaleString()}
                        </div>
                        {iWon && (
                            <button className="btn-pay" onClick={() => setWinningItem(auction)} style={{width:'100%'}}>
                                Complete Payment
                            </button>
                        )}
                    </div>
                ) : (
                    <>
                        <span style={{fontSize: '0.9rem', color: '#888', textTransform: 'uppercase', letterSpacing:'1px'}}>Current Price</span>
                        <div style={{fontSize: '3.5rem', fontWeight: 'bold', color: '#2563eb', margin: '10px 0'}}>
                            ${price.toLocaleString()}
                        </div>
                        <div style={{display: 'flex', justifyContent: 'center', gap: '5px', color: '#d97706', fontWeight:'500'}}>
                            <Clock size={20}/> <span>Ends in: {auction.timeLeft || "Active"}</span>
                        </div>

                        {/* BIDDING CONTROLS */}
                        <div style={{marginTop: '30px'}}>
                            {isDutch ? (
                                <button 
                                    className="btn-buy-now"
                                    style={{width: '100%', padding: '15px', fontSize: '1.2rem'}}
                                    onClick={handlePlaceBid}
                                >
                                    <ShoppingCart size={20} style={{verticalAlign:'middle', marginRight:'8px'}}/>
                                    BUY NOW
                                </button>
                            ) : (
                                <form onSubmit={handlePlaceBid} style={{display: 'flex', gap: '10px'}}>
                                    <input
                                        type="number"
                                        step="0.01"
                                        className="bid-input"
                                        style={{padding: '15px', fontSize: '1.2rem', width: '100%'}}
                                        placeholder="Enter amount..."
                                        value={bidAmount}
                                        onChange={(e) => setBidAmount(e.target.value)}
                                    />
                                    <button type="submit" className="btn-bid" style={{padding: '0 40px'}}>
                                        BID
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
                <p>You won the <strong>{title}</strong>!</p>
                <div className="modal-price">Pay: ${price}</div>
                <button className="btn-pay" onClick={handlePaymentRedirect}>
                    <CreditCard size={18} style={{marginRight:'8px'}}/> Proceed to Payment
                </button>
                <button className="btn-close-modal" onClick={() => setWinningItem(null)}>Close</button>
            </div>
        </div>
      )}
    </div>
  );
};

export default AuctionPage;