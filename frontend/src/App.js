import React, { useState } from 'react';
import { Lock } from 'lucide-react';
import { ReactComponent as AuctionLogo } from './logo.svg';
import AuthenticationUI from './components/AuthenticationUI';
import HomePage from './components/HomePage';
import ChatAssistant from './components/ChatbotAssistant';
import CataloguePage from './components/Pages/CataloguePage'; 
import AuctionPage from './components/Pages/AuctionPage'; 
import PurchasePage from './components/Pages/PurchasePage';
import ReceiptPage from './components/Pages/ReceiptPage';
import SellerUploadPage from './components/Pages/SellerUploadPage';

import "./styles/AuctionStyle.css";

function App() {
  // Restore auth from localStorage on page load
  const [userId, setUserId] = useState(() => {
    const saved = localStorage.getItem('userId');
    return saved ? JSON.parse(saved) : null;
  });
  const [token, setToken] = useState(() => localStorage.getItem('token') || null);
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem('user');
    return saved ? JSON.parse(saved) : null;
  });

  // Start at 'home' or 'catalogue' if already logged in
  const [currentPage, setCurrentPage] = useState(() => 
    localStorage.getItem('token') ? 'catalogue' : 'home'
  );

  const [selectedItem, setSelectedItem] = useState(null);
  const [receiptData, setReceiptData] = useState(null);

  const navigateTo = (page) => setCurrentPage(page);

  const handleLogout = () => {
    // Clear state
    setUserId(null);
    setToken(null);
    setUser(null);
    setSelectedItem(null);
    setCurrentPage("home");
    
    // Clear localStorage
    localStorage.removeItem('userId');
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  };

  const handleLoginSuccess = (id, userToken, userData) => {
    // Update state
    setUserId(id);
    setToken(userToken);
    setUser(userData);
    setCurrentPage("catalogue");
    
    // Persist to localStorage
    localStorage.setItem('userId', JSON.stringify(id));
    localStorage.setItem('token', userToken);
    localStorage.setItem('user', JSON.stringify(userData));
  };

  // --- HANDLERS ---

  // 1. Catalogue -> Single Item Auction Room
  const handleViewAuction = (item) => {
    setSelectedItem(item);
    setCurrentPage("auction_room"); // Switches to Single Item View
  };

  // 2. Auction Room -> Payment
  const handleProceedToPayment = (item) => {
    setSelectedItem(item);
    setCurrentPage("purchase");
  };

  // 3. Payment -> Receipt
  const handlePurchaseSuccess = (data) => {
    setReceiptData(data);

    if (selectedItem) {
      setSelectedItem({ ...selectedItem, closed: true, ended: true });
    }

    setCurrentPage("receipt");
  };

  return (
    <div className="App">
      <nav className="navbar">
        <div className="nav-brand" onClick={() => navigateTo('home')} style={{ display: 'flex', alignItems: 'center', cursor: 'pointer' }}>
            <AuctionLogo style={{ height: '50px', width: 'auto' }} />
        </div>
        <div className="nav-links">
          <span className="nav-item" onClick={() => navigateTo('home')}>Home</span>
          <span className="nav-item" onClick={() => navigateTo('catalogue')}>Catalogue</span>
          
          {/* Upload Item Link */}
          <span className="nav-item" onClick={() => navigateTo('upload')}>Sell Item</span>

          {userId ? (
            <span className="nav-item" onClick={handleLogout}>
              Logout
            </span>
          ) : (
            <span className="nav-item" onClick={() => navigateTo("auth")}>
              Sign In
            </span>
          )}
        </div>
      </nav>

      {/* --- VIEWS --- */}

      {currentPage === "home" && (
        <HomePage onStart={() => navigateTo("catalogue")} />
      )}

      {currentPage === "auth" && (
        <AuthenticationUI onLogin={handleLoginSuccess} />
      )}

      {/* UPLOAD PAGE  */}
      {currentPage === 'upload' && (
          userId ? (
               <SellerUploadPage user={user} token={token} />

          ) : (
              // Login Modal if not signed in
              <div className="modal-overlay">
                <div className="modal-content">
                  <div className="modal-icon" style={{ background: '#eff6ff', color: '#2563eb' }}>
                    <Lock size={32} />
                  </div>
                  <h2 className="modal-title">Authentication Required</h2>
                  <p style={{ color: '#666', marginBottom: '20px' }}>
                    You must be signed in to upload a catalogue item.
                  </p>
                  <button className="btn-pay" style={{ background: '#2563eb' }} onClick={() => navigateTo('auth')}>
                    Go to Sign In
                  </button>
                  <button className="btn-close-modal" onClick={() => navigateTo('catalogue')}>
                    Cancel
                  </button>
                </div>
              </div>
              
          )
      )}

      {/* CATALOGUE (Gallery View) */}
      {currentPage === 'catalogue' && (
          <CataloguePage
              // This function handles the click on a card
              onSelectItem={handleViewAuction} 
          />
      )}

      {/* AUCTION ROOM (Single Item View) */}
      {currentPage === 'auction_room' && (
        <AuctionPage 
            item={selectedItem}
            currentUserId={userId} 
            token={token}
            onRequestLogin={() => navigateTo('auth')}
            onBack={() => navigateTo('catalogue')}
            onBuyNow={handleProceedToPayment} 
        />
      )}

      {/* PURCHASE FLOW */}
      {currentPage === "purchase" && (
        <PurchasePage
          item={selectedItem}
          userId={userId}
          token={token}
          user={user}
          onSuccess={handlePurchaseSuccess}
          setAuctionEnded={(ended) =>
            setSelectedItem({ ...selectedItem, ended: ended })
          }
          onLogout={handleLogout}
        />
      )}

      {currentPage === "receipt" && (
        <ReceiptPage
          data={receiptData}
          user={user}
          onBackToHome={() => navigateTo("catalogue")}
        />
      )}

      {/* AI CHATBOT */}
      <ChatAssistant
        token={token}
        userId={userId}
        onNavigate={(page) => setCurrentPage(page)}
      />
    </div>
  );
}
export default App;
