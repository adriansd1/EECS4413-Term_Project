import { useState, useEffect } from "react";
import {
  CreditCard,
  User,
  MapPin,
  Lock,
  Calendar,
  ShieldCheck,
  LogOut,
} from "lucide-react";

const PurchasePage = ({ item, userId, token, user, onSuccess, onLogout, setAuctionEnded }) => {
  //Purchases purchase = new Purchases(request.item, request.amount, request.price, user, request.cardNumber, request.cardExpiry, request.cardCvv);
  //User(String username, String password, String firstName, String lastName, String shippingAddress, String email)

  const [itemDetails, setItemDetails] = useState({
    item: item.name,
    amount: 1,
    price: item.currentBid,
    userId: userId,
    cardNumber: "",
    cardCvv: "",
    cardExpiry: "",
  });

  useEffect(() => {
    console.log("Item in PurchasePage:", item);
  }, []);

  const handleChange = (e) => {
    e.preventDefault();
    const { name, value } = e.target;
    setItemDetails((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    console.log("Processing Payment...", itemDetails);

    try {
      const res = await fetch(
        "http://localhost:8080/api/purchases/makePurchase",
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(itemDetails),
        }
      );

      if (res.status === 200) {
        const data = await res.json();
        console.log("Success:", data);
        const tax = item.currentBid * 0.13;
        const total = item.currentBid + tax;

        const purchaseID = data.purchaseId;

        console.log("purchaseID: ", purchaseID);

        const receiptPayload = {
          purchaseId: purchaseID,
          //TODO: change to userId to ownerID once Adrian finishes the implementations
          owner_id: userId,
          //mock value for now
          shippingDays: 3,
          auctionId: item.id,
        };

        const receiptRes = await fetch(
          "http://localhost:8080/api/receipts/createReceipt", // Adjust path if your Controller mapping differs
          {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(receiptPayload),
          }
        );

        if (!receiptRes.ok) {
          throw new Error(
            "Receipt creation failed: " + (await receiptRes.text())
          );
        }

        setAuctionEnded(true);
        const receiptResponseText = await receiptRes.text();
        console.log("Receipt API Response:", receiptResponseText);

        const fullReceiptData = {
          transactionId: purchaseID,
          date: new Date().toLocaleString(),
          buyer: {
            name: user.firstName + " " + user.lastName,
            address: user.shippingAddress || "456 Buyer St, Shopper City",
          },
          seller: {
            name: item.ownerName || "Auction House",
            address: "123 Seller Lane, Commerce City",
          },
          item: {
            name: item.name,
            priceBeforeTax: item.currentHighestBid,
          },
          taxAmount: tax,
          totalPrice: total,
          cardTail: itemDetails.cardNumber.slice(-4),
        };

        console.log("Full Receipt Data:", fullReceiptData);
        onSuccess(fullReceiptData);
      } else {
        const errorText = await res.text();
        console.error("Error:", errorText);
        alert("Error: " + errorText);
      }
    } catch (error) {
      console.error("Transaction Failed:", error);
      alert("Transaction Failed: " + error.message);
      alert("Failed to connect to the server.");
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-600 to-purple-700 flex items-center justify-center p-4 sm:p-6">
      <div className="bg-white w-full max-w-lg rounded-2xl shadow-2xl overflow-hidden">
        {/* USER HEADER BAR */}
        {userId && onLogout && (
          <div
            style={{
              background: "#f8fafc",
              borderBottom: "1px solid #e2e8f0",
              padding: "12px 20px",
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
            }}
          >
            <div
              style={{
                display: "flex",
                alignItems: "center",
                gap: "8px",
                color: "#64748b",
                fontSize: "13px",
              }}
            >
              <User size={14} />
              <span>User ID: {userId}</span>
            </div>
            <button
              onClick={onLogout}
              style={{
                display: "flex",
                alignItems: "center",
                gap: "6px",
                padding: "6px 12px",
                background: "#ef4444",
                color: "white",
                border: "none",
                borderRadius: "6px",
                cursor: "pointer",
                fontSize: "12px",
                fontWeight: "500",
              }}
            >
              <LogOut size={13} />
              Sign Out
            </button>
          </div>
        )}

        {/* Header */}
        <div className="bg-gray-50 px-8 py-6 border-b border-gray-100">
          <h2 className="text-2xl font-bold text-gray-800 flex items-center gap-2">
            <ShieldCheck className="text-blue-600" />
            Secure Checkout
          </h2>
          <p className="text-gray-500 text-sm mt-1">
            Complete your purchase for Auction Item #404
          </p>
        </div>

        <form onSubmit={handleSubmit} className="p-8 space-y-6">
          <div>
            <h3 className="text-sm font-semibold text-gray-400 uppercase tracking-wider mb-4 flex items-center gap-2">
              <User size={16} /> Personal Information
            </h3>

            <div className="space-y-4">
              {/* Name Input */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Full Name
                </label>
                <input
                  type="text"
                  value={user.firstName + " " + user.lastName}
                  name="fullName"
                  placeholder="Tony Stark"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all"
                  onChange={handleChange}
                  disabled
                />
              </div>

              {/* Email Input */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Email Address
                </label>
                <input
                  type="email"
                  //value={user.email}
                  name="email"
                  placeholder="john@example.com"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all"
                  onChange={handleChange}
                  //disabled
                />
              </div>

              {/* Address Grid */}
              <div className="grid grid-cols-2 gap-4">
                <div className="col-span-2">
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Address
                  </label>
                  <div className="relative">
                    <MapPin
                      className="absolute left-3 top-2.5 text-gray-400"
                      size={18}
                    />
                    <input
                      type="text"
                      name="address"
                      //value={user.shippingAddress}
                      placeholder="123 Market St"
                      className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                      onChange={handleChange}
                      //disabled
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>

          <hr className="border-gray-100" />

          <div>
            <h3 className="text-sm font-semibold text-gray-400 uppercase tracking-wider mb-4 flex items-center gap-2">
              <CreditCard size={16} /> Payment Details
            </h3>

            <div className="space-y-4">
              {/* Card Number */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Card Number
                </label>
                <div className="relative">
                  <CreditCard
                    className="absolute left-3 top-2.5 text-gray-400"
                    size={18}
                  />
                  <input
                    type="text"
                    name="cardNumber"
                    placeholder="0000 0000 0000 0000"
                    maxLength="19"
                    className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                    onChange={handleChange}
                  />
                </div>
              </div>

              {/* Expiry and CVC Grid */}
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Expiry Date
                  </label>
                  <div className="relative">
                    <Calendar
                      className="absolute left-3 top-2.5 text-gray-400"
                      size={18}
                    />
                    <input
                      type="text"
                      name="cardExpiry"
                      placeholder="MM / YY"
                      className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                      onChange={handleChange}
                    />
                  </div>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    CVC
                  </label>
                  <div className="relative">
                    <Lock
                      className="absolute left-3 top-2.5 text-gray-400"
                      size={18}
                    />
                    <input
                      type="text"
                      name="cardCvv"
                      placeholder="123"
                      maxLength="3"
                      className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                      onChange={handleChange}
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* Submit Button */}
          <button
            type="submit"
            className="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-4 rounded-lg transition-colors shadow-lg mt-4"
          >
            Pay Now
          </button>
        </form>
      </div>
    </div>
  );
};

export default PurchasePage;
