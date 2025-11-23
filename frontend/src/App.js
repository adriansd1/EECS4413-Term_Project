import AuthenticationUI from "./components/AuthenticationUI";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import PurchasePage from "./components/Pages/PurchasePage";
import ReceiptPage from "./components/Pages/ReceiptPage";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/purchase" element={<PurchasePage />} />
        <Route path="/receipt" element={<ReceiptPage />} />
        <Route path="/" element={<AuthenticationUI className="App" />} />
      </Routes>
    </Router>
  );
}

export default App;
