import React, { useEffect, useState } from "react";
import {
  CreditCard,
  User,
  MapPin,
  Lock,
  Calendar,
  ShieldCheck,
  ArrowLeft,
  Gavel,
  DollarSign,
} from "lucide-react";

const PurchasePage = ({ item, onBack, onSuccess }) => {
  const [itemDetails, setItemDetails] = useState({
    item: "",
    amount: 1,
    price: "",
    userId: "",
    cardNumber: "",
    cardCvv: "",
    cardExpiry: "",
  });

  useEffect(() => {
    setItemDetails((prev) => ({
      ...prev,
      item: item.itemName,
      price: item.currentHighestBid,
      userId: item.currentHighestBidder.id,
    }));
  }, []);

  const handleChange = (e) => {
    e.preventDefault();
    const { name, value } = e.target;
    if (name == "cardCvv" || name == "cardNumber" || name == "cardExpiry") {
      setItemDetails((prev) => ({ ...prev, [name]: value }));
    }
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

      if (res.ok) {
        const data = await res.json();
        console.log("Success:", data);
        const tax = item.currentHighestBid * 0.13;
        const total = item.currentHighestBid + tax;

        const purchaseID = data.purchaseId;

        console.log("purchaseID: ", purchaseID);

        const receiptPayload = {
          purchaseId: purchaseID,
          //mock value for now
          owner_id: 999,
          //mock value for now
          shippingDays: 3,
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

        const receiptResponseText = await receiptRes.text();
        console.log("Receipt API Response:", receiptResponseText);

        const fullReceiptData = {
          transactionId: purchaseID,
          date: new Date().toLocaleString(),
          buyer: {
            name: item.currentHighestBidder.fullName,
            address: item.currentHighestBidder.shippingAddress,
          },
          seller: {
            name: item.ownerName || "Auction House",
            address: "123 Seller Lane, Commerce City",
          },
          item: {
            name: item.itemName,
            priceBeforeTax: item.currentHighestBid,
          },
          taxAmount: tax,
          totalPrice: total,
          cardTail: itemDetails.cardNumber.slice(-4),
        };

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
    <div className="min-h-screen bg-gradient-to-br from-blue-600 to-purple-700 flex items-center justify-center p-4 sm:p-6 font-sans">
      <div className="bg-white w-full max-w-lg rounded-2xl shadow-2xl overflow-hidden animate-fade-in-up">
        {/* Header */}
        <div className="bg-gray-50 px-8 py-6 border-b border-gray-100">
          <button
            onClick={onBack}
            className="text-gray-400 hover:text-blue-600 flex items-center gap-1 text-sm mb-4 transition-colors"
          >
            <ArrowLeft size={16} /> Back to Auction
          </button>

          <h2 className="text-2xl font-bold text-gray-800 flex items-center gap-2">
            <ShieldCheck className="text-blue-600" />
            Secure Checkout
          </h2>
          <div className="mt-2 p-3 bg-blue-50 rounded-lg border border-blue-100">
            <p className="text-gray-500 text-xs uppercase tracking-wide font-bold">
              Purchasing
            </p>
            <div className="flex justify-between items-center mt-1">
              <span className="text-gray-800 font-medium">{item.itemName}</span>
              <span className="text-blue-600 font-bold text-lg">
                ${item.currentHighestBid}
              </span>
            </div>
          </div>
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
                  required
                  type="text"
                  name="fullName"
                  placeholder="John Doe"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all"
                  onChange={handleChange}
                />
              </div>

              {/* Email Input */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Email Address
                </label>
                <input
                  required
                  type="email"
                  name="email"
                  placeholder="john@example.com"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all"
                  onChange={handleChange}
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
                      required
                      type="text"
                      name="address"
                      placeholder="123 Market St"
                      className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                      onChange={handleChange}
                    />
                  </div>
                </div>
                <div>
                  <input
                    required
                    type="text"
                    name="city"
                    placeholder="City"
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                    onChange={handleChange}
                  />
                </div>
                <div>
                  <input
                    required
                    type="text"
                    name="zipCode"
                    placeholder="ZIP / Postal"
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                    onChange={handleChange}
                  />
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
                    required
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
                      required
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
                    CVV
                  </label>
                  <div className="relative">
                    <Lock
                      className="absolute left-3 top-2.5 text-gray-400"
                      size={18}
                    />
                    <input
                      required
                      type="password"
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
            className="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-4 rounded-lg transition-colors shadow-lg mt-4 flex justify-between items-center"
          >
            <span>Pay Securely</span>
            <span>${item.currentHighestBid}</span>
          </button>
        </form>
      </div>
    </div>
  );
};

export default PurchasePage;
