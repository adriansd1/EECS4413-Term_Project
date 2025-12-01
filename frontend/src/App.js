import React, { useState } from "react";
import { Lock } from "lucide-react";

import AuthenticationUI from "./components/AuthenticationUI";
import HomePage from "./components/HomePage";
import ChatAssistant from "./components/ChatbotAssistant";
import CataloguePage from "./components/Pages/CataloguePage";
import AuctionPage from "./components/Pages/AuctionPage";
import PurchasePage from "./components/Pages/PurchasePage";
import ReceiptPage from "./components/Pages/ReceiptPage";
import SellerUploadPage from "./components/Pages/SellerUploadPage";

import "./styles/AuctionStyle.css";

function App() {
  const [userId, setUserId] = useState(null);
  const [token, setToken] = useState(null);
  const [user, setUser] = useState(null);

  // Start at 'home'
  const [currentPage, setCurrentPage] = useState("home");

  const [selectedItem, setSelectedItem] = useState(null);
  const [receiptData, setReceiptData] = useState(null);

  const navigateTo = (page) => setCurrentPage(page);

  const handleLogout = () => {
    setUserId(null);
    setToken(null);
    setUser(null);
    setSelectedItem(null);
    setCurrentPage("home");
  };

  const handleLoginSuccess = (id, userToken, userData) => {
    setUserId(id);
    setToken(userToken);
    setUser(userData);
    setCurrentPage("catalogue"); // Go to catalogue after login
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
        <div className="nav-brand" onClick={() => navigateTo("home")}>
          Auction404
        </div>
        <div className="nav-links">
          <span className="nav-item" onClick={() => navigateTo("home")}>
            Home
          </span>
          <span className="nav-item" onClick={() => navigateTo("catalogue")}>
            Catalogue
          </span>

          {/* ✅ Upload Item Link */}
          <span className="nav-item" onClick={() => navigateTo("upload")}>
            Sell Item
          </span>

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

      {/* ✅ UPLOAD PAGE (Replaced CreateAuction) */}
      {currentPage === "upload" &&
        (userId ? (
          <SellerUploadPage userId={userId} token={token} />
        ) : (
          // Login Modal if not signed in
          <div className="modal-overlay">
            <div className="modal-content">
              <div
                className="modal-icon"
                style={{ background: "#eff6ff", color: "#2563eb" }}
              >
                <Lock size={32} />
              </div>
              <h2 className="modal-title">Authentication Required</h2>
              <p style={{ color: "#666", marginBottom: "20px" }}>
                You must be signed in to upload a catalogue item.
              </p>
              <button
                className="btn-pay"
                style={{ background: "#2563eb" }}
                onClick={() => navigateTo("auth")}
              >
                Go to Sign In
              </button>
              <button
                className="btn-close-modal"
                onClick={() => navigateTo("catalogue")}
              >
                Cancel
              </button>
            </div>
          </div>
        ))}

      {/* ✅ CATALOGUE (Gallery View) */}
      {currentPage === "catalogue" && (
        <CataloguePage
          // This function handles the click on a card
          onSelectItem={handleViewAuction}
        />
      )}

      {/* ✅ AUCTION ROOM (Single Item View) */}
      {currentPage === "auction_room" && (
        <AuctionPage
          item={selectedItem}
          currentUserId={userId}
          token={token}
          onRequestLogin={() => navigateTo("auth")}
          onBack={() => navigateTo("catalogue")}
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
