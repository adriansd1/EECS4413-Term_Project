import React, { useState } from 'react';
import AuthenticationUI from './components/AuthenticationUI';
import AuctionPage from './components/AuctionPage';
import HomePage from './components/HomePage';
import './styles/AuctionStyle.css'; 

function App() {
  // Force log in as User ID 1
  // only for testing purpose otherwise set useState to 0
  const [userId, setUserId] = useState(1);
  const [currentPage, setCurrentPage] = useState('home'); 

  const navigateTo = (page) => {
    setCurrentPage(page);
  };

  const handleLogout = () => {
    setUserId(null);
    setCurrentPage('home');
  };

  const handleLoginSuccess = (id) => {
    setUserId(id);
    setCurrentPage('auctions');
  };

  return (
    <div className="App">
      {/* --- NAVBAR --- */}
      <nav className="navbar">
        <div className="nav-brand" onClick={() => navigateTo('home')}>
          Auction404
        </div>
        <div className="nav-links">
          <span className="nav-item" onClick={() => navigateTo('home')}>Home</span>
          
          {/* ✅ CHANGE 1: Always allow going to Auctions, even if not logged in */}
          <span className="nav-item" onClick={() => navigateTo('auctions')}>
            Auctions
          </span>

          {userId ? (
            <span className="nav-item" onClick={handleLogout}>Logout</span>
          ) : (
            <span className="nav-item" onClick={() => navigateTo('auth')}>Sign In</span>
          )}
        </div>
      </nav>

      {/* --- PAGE CONTENT --- */}
      {currentPage === 'home' && (
        <HomePage onStart={() => navigateTo('auctions')} />
      )}

      {currentPage === 'auth' && (
        <AuthenticationUI onLogin={handleLoginSuccess} />
      )}

      {/* ✅ CHANGE 2: Render AuctionPage even if userId is null */}
      {currentPage === 'auctions' && (
        <AuctionPage 
            currentUserId={userId} 
            onRequestLogin={() => navigateTo('auth')} 
        />
      )}
    </div>
  );
}

export default App;
