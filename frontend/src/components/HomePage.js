import React from 'react';
import '../styles/AuctionStyle.css';

const HomePage = ({ onStart }) => {
  return (
    <div className="app-container" style={{ display: 'block' }}>
      {/* Hero Section */}
      <div className="hero-container">
        <h1 className="hero-title">Auction404</h1>
        <p className="hero-subtitle">
          The safest place to buy, sell, and bid on unique items.
          <br />Real-time bidding. Secure payments. Verified users.
        </p>
        
        <button className="btn-hero" onClick={onStart}>
          Start Bidding Now
        </button>

        {/* Features Grid */}
        <div className="features-grid">
          <div className="feature-card">
            <h3>🚀 Fast Bidding</h3>
            <p style={{fontSize: '14px', opacity: 0.8}}>
              Real-time updates ensure you never miss a deal.
            </p>
          </div>
          <div className="feature-card">
            <h3>🔒 Secure</h3>
            <p style={{fontSize: '14px', opacity: 0.8}}>
              Your transactions and data are protected by industry standards.
            </p>
          </div>
          <div className="feature-card">
            <h3>🏆 Win Big</h3>
            <p style={{fontSize: '14px', opacity: 0.8}}>
              Find rare items at prices you determine.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default HomePage;