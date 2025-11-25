import React, { useState, useEffect } from "react";
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
  CheckCircle,
  ShoppingBag,
  Store,
} from "lucide-react";

const ReceiptPage = ({ data, onReturnHome }) => {
  const formatCurrency = (amount) => {
    return new Intl.NumberFormat("en-US", {
      style: "currency",
      currency: "USD",
    }).format(amount);
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-600 to-purple-700 flex items-center justify-center p-4 sm:p-6 animate-fade-in-up font-sans">
      <div className="bg-white w-full max-w-lg rounded-2xl shadow-2xl overflow-hidden">
        {/* HEADER */}
        <div className="bg-green-50 px-8 py-8 border-b border-green-100 flex flex-col items-center text-center">
          <div className="bg-green-100 p-3 rounded-full mb-4">
            <CheckCircle
              className="text-green-600"
              size={48}
              strokeWidth={1.5}
            />
          </div>
          <h2 className="text-2xl font-bold text-gray-800">
            Payment Successful!
          </h2>
          <p className="text-gray-500 text-sm mt-2">
            Thank you for your purchase.
          </p>
          <div className="flex items-center gap-1 mt-4 text-sm text-gray-500 bg-white px-3 py-1 rounded-full border border-gray-200">
            <Calendar size={14} />
            <span>{data.date}</span>
            <span className="mx-1">•</span>
            <span className="font-mono text-xs">
              {data.transactionId.substring(0, 8)}...
            </span>
          </div>
        </div>

        {/* BODY */}
        <div className="p-8 space-y-8">
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
            <div className="space-y-2">
              <h3 className="text-xs font-semibold text-gray-400 uppercase tracking-wider flex items-center gap-1 mb-1">
                <Store size={14} /> Seller
              </h3>
              <p className="font-semibold text-gray-800">{data.seller.name}</p>
              <p className="text-sm text-gray-500 flex items-start gap-1 leading-relaxed">
                <MapPin size={14} className="mt-1 shrink-0 text-gray-400" />
                {data.seller.address}
              </p>
            </div>

            <div className="space-y-2">
              <h3 className="text-xs font-semibold text-gray-400 uppercase tracking-wider flex items-center gap-1 mb-1">
                <User size={14} /> Buyer
              </h3>
              <p className="font-semibold text-gray-800">{data.buyer.name}</p>
              <p className="text-sm text-gray-500 flex items-start gap-1 leading-relaxed">
                <MapPin size={14} className="mt-1 shrink-0 text-gray-400" />
                {data.buyer.address}
              </p>
            </div>
          </div>

          <hr className="border-dashed border-gray-200" />

          <div>
            <h3 className="text-sm font-bold text-gray-800 mb-4 flex items-center gap-2">
              <ShoppingBag size={18} className="text-blue-600" /> Purchase
              Summary
            </h3>

            <div className="bg-gray-50 rounded-lg p-4 space-y-3 text-sm">
              <div className="flex justify-between items-start">
                <div>
                  <span className="text-gray-700 font-medium block">
                    {data.item.name}
                  </span>
                  <span className="text-gray-400 text-xs">Item Price</span>
                </div>
                <span className="font-semibold text-gray-800">
                  {formatCurrency(data.item.priceBeforeTax)}
                </span>
              </div>
              <div className="flex justify-between text-gray-500">
                <span>Tax / Fees (13%)</span>
                <span>{formatCurrency(data.taxAmount)}</span>
              </div>
              <hr className="border-gray-200 my-2" />
              <div className="flex justify-between text-base font-bold text-gray-900 items-center">
                <span>Total Paid</span>
                <span className="text-xl text-blue-700">
                  {formatCurrency(data.totalPrice)}
                </span>
              </div>
            </div>

            <div className="flex items-center justify-center gap-2 text-sm text-gray-500 mt-6 bg-blue-50 py-2 rounded-md border border-blue-100">
              <CreditCard size={16} className="text-blue-500" />
              <span>
                Paid securely with card ending in{" "}
                <span className="font-bold text-gray-700">
                  xxxx-{data.cardTail}
                </span>
              </span>
            </div>
          </div>

          <button
            onClick={onReturnHome}
            className="w-full bg-gray-900 hover:bg-black text-white py-3 rounded-lg font-medium transition-colors"
          >
            Return to Auctions
          </button>
        </div>
      </div>
    </div>
  );
};

export default ReceiptPage;
