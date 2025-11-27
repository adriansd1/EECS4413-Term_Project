import React, { useState } from 'react';
import React, { useState } from 'react';
import AuthenticationUI from './components/AuthenticationUI';
import HomePage from './components/HomePage';
import CreateAuction from './components/CreateAuction';
import CataloguePage from './components/Pages/CataloguePage'; // Read Only Gallery
import AuctionPage from './components/Pages/AuctionPage';     // Live Bidding List
import PurchasePage from './components/Pages/PurchasePage';
import ReceiptPage from './components/Pages/ReceiptPage';

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
          
          {/* ✅ SEPARATE TABS */}
          <span className="nav-item" onClick={() => navigateTo('catalogue')}>Catalogue</span>
          <span className="nav-item" onClick={() => navigateTo('auctions')}>Live Auctions</span>
          
          <span className="nav-item" onClick={() => navigateTo('create')}>Sell Item</span>

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

      {currentPage === 'create' && (
          userId ? <CreateAuction token={token} onAuctionCreated={() => navigateTo('auctions')} />
                 : <div className="center-msg">Please Login to Sell</div>
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
    </div>
  );
}
export default App;