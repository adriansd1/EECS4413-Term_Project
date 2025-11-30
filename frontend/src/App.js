import React, { useState } from 'react';
import { Lock } from 'lucide-react'; // ✅ Import Lock icon for the popup

import AuthenticationUI from './components/AuthenticationUI';
import HomePage from './components/HomePage';
import CreateAuction from './components/CreateAuction';
import ChatAssistant from './components/ChatbotAssistant';
// ✅ Preserved your specific folder paths
import CataloguePage from './components/Pages/CataloguePage'; 
import AuctionPage from './components/Pages/AuctionPage';     
import PurchasePage from './components/Pages/PurchasePage';
import ReceiptPage from './components/Pages/ReceiptPage';
import SellerUploadPage from './components/Pages/SellerUploadPage';

import './styles/AuctionStyle.css'; 

function App() {
  const [userId, setUserId] = useState(null);
  const [token, setToken] = useState(null);
  
  // Default to 'auctions' so users see live items immediately
  const [currentPage, setCurrentPage] = useState('home'); 
  
  const [selectedItem, setSelectedItem] = useState(null);
  const [receiptData, setReceiptData] = useState(null);

  const navigateTo = (page) => setCurrentPage(page);

  const handleLogout = () => {
    setUserId(null);
    setToken(null);
    setCurrentPage('home');
  };

  const handleLoginSuccess = (id, userToken) => {
    setUserId(id);
    setToken(userToken);
    setCurrentPage('auctions'); // Go to live auctions after login
  };

  // Handle "Pay Now" from Auction Page
  const handleProceedToPayment = (item) => {
    setSelectedItem(item);
    setCurrentPage('purchase');
  };

  // Handle Payment Success
  const handlePurchaseSuccess = (data) => {
    setReceiptData(data);
    setCurrentPage('receipt');
  };

  return (
    <div className="App">
      <nav className="navbar">
        <div className="nav-brand" onClick={() => navigateTo('home')}>Auction404</div>
        <div className="nav-links">
          <span className="nav-item" onClick={() => navigateTo('home')}>Home</span>

          <span className="nav-item" onClick={() => navigateTo('catalogue')}>Catalogue</span>
          <span className="nav-item" onClick={() => navigateTo('auctions')}>Live Auctions</span>

          <span className="nav-item" onClick={() => navigateTo('create')}>Sell Item</span>

          <span className="nav-item" onClick={() => navigateTo('upload')}>
            Upload Item
          </span>

          {userId ? (
              <span className="nav-item" onClick={handleLogout}>Logout</span>
          ) : (
              <span className="nav-item" onClick={() => navigateTo('auth')}>Sign In</span>
          )}
        </div>
      </nav>

      {/* VIEWS */}
      {currentPage === 'home' && <HomePage onStart={() => navigateTo('auctions')} />}
      
      {currentPage === 'auth' && <AuthenticationUI onLogin={handleLoginSuccess} />}

      {currentPage === 'upload' && (
          userId ? (
              <SellerUploadPage userId={userId} token={token} />
          ) : (
              <div className="modal-overlay">
                <div className="modal-content">
                  <div className="modal-icon" style={{ background: '#eff6ff', color: '#2563eb' }}>
                    <Lock size={32} />
                  </div>

                  <h2 className="modal-title">Authentication Required</h2>
                  <p style={{ color: '#666', marginBottom: '20px' }}>
                    You must be signed in to upload a catalogue item.
                  </p>

                  <button
                      className="btn-pay"
                      style={{ background: '#2563eb' }}
                      onClick={() => navigateTo('auth')}
                  >
                    Go to Sign In
                  </button>

                  <button className="btn-close-modal" onClick={() => navigateTo('auctions')}>
                    Cancel
                  </button>
                </div>
              </div>
          )
      )}


      {/* ✅ SELL ITEM PAGE (With Login Modal) */}
      {currentPage === 'create' && (
          userId ? (
            <CreateAuction token={token} onAuctionCreated={() => navigateTo('auctions')} />
          ) : (
            // ✨ NEW LOGIN POPUP (Uses existing styles from AuctionPage)
            <div className="modal-overlay">
                <div className="modal-content">
                    <div className="modal-icon" style={{background: '#eff6ff', color: '#2563eb'}}>
                        <Lock size={32} />
                    </div>
                    <h2 className="modal-title">Authentication Required</h2>
                    <p style={{color: '#666', marginBottom: '20px'}}>
                        You must be signed in to list an item for auction.
                    </p>
                    
                    <button 
                        className="btn-pay" 
                        style={{background: '#2563eb'}} // Override green to blue
                        onClick={() => navigateTo('auth')}
                    >
                        Go to Sign In
                    </button>

                    <button 
                        className="btn-close-modal" 
                        onClick={() => navigateTo('auctions')}
                    >
                        Cancel
                    </button>
                </div>
            </div>
          )
      )}

      {/* 1. CATALOGUE: READ ONLY */}
      {currentPage === 'catalogue' && <CataloguePage />}

      {/* 2. AUCTIONS: LIVE BIDDING LIST */}
      {currentPage === 'auctions' && (
        <AuctionPage 
            currentUserId={userId} 
            token={token}
            onRequestLogin={() => navigateTo('auth')}
            onBuyNow={handleProceedToPayment} // Triggers payment flow
        />
      )}

      {/* CHECKOUT FLOW */}
      {currentPage === 'purchase' && (
          <PurchasePage item={selectedItem} userId={userId} token={token} onSuccess={handlePurchaseSuccess} />
      )}
      
      {currentPage === 'receipt' && (
          <ReceiptPage data={receiptData} onBackToHome={() => navigateTo('auctions')} />
      )}
      {/* ✅ AI CHATBOT (Always Visible) */}
    <ChatAssistant 
        token={token} 
        userId={userId} 
        onNavigate={(page) => setCurrentPage(page)} 
    />
    </div>
  );
}
export default App;