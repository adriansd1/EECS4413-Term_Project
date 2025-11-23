import React from "react";
import {
  CheckCircle,
  User,
  MapPin,
  CreditCard,
  ShoppingBag,
  Calendar,
  Store,
} from "lucide-react";

// MOCK DATA - In a real app, this would be passed in as props
const receiptData = {
  transactionId: "TXN-882459102",
  date: "Oct 28, 2023, 10:30 AM",
  buyer: {
    name: "Alex Johnson",
    address: "123 Maple Ave, Suite 400, Toronto, ON M5V 2T6",
  },
  seller: {
    name: "Retro Auction House Ltd.",
    address: "456 Vintage Ln, New York, NY 10001, USA",
  },
  item: {
    name: "Vintage 1985 Macintosh Classic (Auction #404)",
    priceBeforeTax: 450.0,
  },
  taxAmount: 58.5,
  totalPrice: 508.5,
  cardTail: "4242",
};

const ReceiptPage = () => {
  // Helper to format currency
  const formatCurrency = (amount) => {
    return new Intl.NumberFormat("en-US", {
      style: "currency",
      currency: "USD",
    }).format(amount);
  };

  return (
    // MAIN CONTAINER - Same gradient background
    <div className="min-h-screen bg-gradient-to-br from-blue-600 to-purple-700 flex items-center justify-center p-4 sm:p-6">
      {/* RECEIPT CARD - Same card style */}
      <div className="bg-white w-full max-w-lg rounded-2xl shadow-2xl overflow-hidden">
        {/* HEADER - Green theme for success */}
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
            <span>{receiptData.date}</span>
            <span className="mx-1">•</span>
            <span>ID: {receiptData.transactionId}</span>
          </div>
        </div>

        {/* BODY CONTENT */}
        <div className="p-8 space-y-8">
          {/* Buyer / Seller Grid */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
            {/* Seller Info */}
            <div className="space-y-2">
              <h3 className="text-xs font-semibold text-gray-400 uppercase tracking-wider flex items-center gap-1 mb-1">
                <Store size={14} /> Seller
              </h3>
              <p className="font-semibold text-gray-800">
                {receiptData.seller.name}
              </p>
              <p className="text-sm text-gray-500 flex items-start gap-1 leading-relaxed">
                <MapPin size={14} className="mt-1 shrink-0 text-gray-400" />
                {receiptData.seller.address}
              </p>
            </div>

            {/* Buyer Info */}
            <div className="space-y-2">
              <h3 className="text-xs font-semibold text-gray-400 uppercase tracking-wider flex items-center gap-1 mb-1">
                <User size={14} /> Buyer
              </h3>
              <p className="font-semibold text-gray-800">
                {receiptData.buyer.name}
              </p>
              <p className="text-sm text-gray-500 flex items-start gap-1 leading-relaxed">
                <MapPin size={14} className="mt-1 shrink-0 text-gray-400" />
                {receiptData.buyer.address}
              </p>
            </div>
          </div>

          <hr className="border-dashed border-gray-200" />

          {/* SUMMARY SECTION */}
          <div>
            <h3 className="text-sm font-bold text-gray-800 mb-4 flex items-center gap-2">
              <ShoppingBag size={18} className="text-blue-600" /> Purchase
              Summary
            </h3>

            <div className="bg-gray-50 rounded-lg p-4 space-y-3 text-sm">
              {/* Item Row */}
              <div className="flex justify-between items-start">
                <div>
                  <span className="text-gray-700 font-medium block">
                    {receiptData.item.name}
                  </span>
                  <span className="text-gray-400 text-xs">Item Price</span>
                </div>
                <span className="font-semibold text-gray-800">
                  {formatCurrency(receiptData.item.priceBeforeTax)}
                </span>
              </div>

              {/* Tax Row */}
              <div className="flex justify-between text-gray-500">
                <span>Tax / Fees</span>
                <span>{formatCurrency(receiptData.taxAmount)}</span>
              </div>

              <hr className="border-gray-200 my-2" />

              {/* Total Row */}
              <div className="flex justify-between text-base font-bold text-gray-900 items-center">
                <span>Total Paid</span>
                <span className="text-xl text-blue-700">
                  {formatCurrency(receiptData.totalPrice)}
                </span>
              </div>
            </div>

            {/* Payment Method Footer */}
            <div className="flex items-center justify-center gap-2 text-sm text-gray-500 mt-6 bg-blue-50 py-2 rounded-md border border-blue-100">
              <CreditCard size={16} className="text-blue-500" />
              <span>
                Paid securely with card ending in{" "}
                <span className="font-bold text-gray-700">
                  xxxx-{receiptData.cardTail}
                </span>
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ReceiptPage;
