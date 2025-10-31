/**
 * Authentication UI Component
 * React component for user sign-up (UC1.1) and sign-in (UC1.2)
 * 
 * Features:
 * - Tab-based interface for sign-up and sign-in
 * - Real-time form validation
 * - Password visibility toggle
 * - Loading states during API calls
 * - Success/error message display
 * - Responsive design with Tailwind CSS
 * 
 * Test Coverage: TC-01 through TC-04
 */

import React, { useState } from 'react';
import { Eye, EyeOff, AlertCircle, CheckCircle } from 'lucide-react';

// Backend API base URL - change this to match your backend server
// Development: http://localhost:8080/api/auth
// Production: https://api.auction404.com/api/auth
const API_BASE_URL = 'http://localhost:8080/api/auth';

function AuthenticationUI() {
  
  // ============================================
  // STATE MANAGEMENT
  // ============================================
  
  /**
   * Active tab state - controls which form is visible
   * Values: 'signin' | 'signup'
   */
  const [activeTab, setActiveTab] = useState('signin');
  
  /**
   * Sign-up form data state
   * Stores all user input for registration (UC1.1)
   * 
   * Fields match SignUpRequest DTO on backend
   */
  const [signUpData, setSignUpData] = useState({
    username: '',
    email: '',
    password: '',
    firstName: '',
    lastName: '',
    shippingAddress: '',
  });
  
  /**
   * Sign-in form data state
   * Stores username and password for authentication (UC1.2)
   * 
   * Fields match SignInRequest DTO on backend
   */
  const [signInData, setSignInData] = useState({
    username: '',
    password: '',
  });
  
  /**
   * Password visibility toggle
   * Controls whether password is shown as text or hidden
   */
  const [showPassword, setShowPassword] = useState(false);
  
  /**
   * Loading state
   * Prevents multiple form submissions and shows loading indicator
   */
  const [loading, setLoading] = useState(false);
  
  /**
   * Message state
   * Displays success/error messages to user
   * 
   * Structure:
   * - type: 'success' | 'error' | 'info'
   * - text: Message content to display
   */
  const [message, setMessage] = useState({ type: '', text: '' });

  // ============================================
  // EVENT HANDLERS
  // ============================================
  
  /**
   * Handle Sign-Up form input changes
   * Updates state as user types in sign-up form fields
   * 
   * @param {Event} e - Input change event
   * 
   * How it works:
   * 1. Extract field name and value from event
   * 2. Update only the changed field in state
   * 3. Preserve other fields using spread operator
   * 
   * Example:
   * User types "j" in username field
   * → e.target.name = "username"
   * → e.target.value = "j"
   * → State updates to { ...signUpData, username: "j" }
   */
  const handleSignUpChange = (e) => {
    const { name, value } = e.target;
    setSignUpData(prev => ({
      ...prev,  // Spread operator: copy existing data
      [name]: value  // Update only the changed field
    }));
  };

  /**
   * Handle Sign-In form input changes
   * Updates state as user types in sign-in form fields
   * 
   * @param {Event} e - Input change event
   */
  const handleSignInChange = (e) => {
    const { name, value } = e.target;
    setSignInData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  /**
   * Handle Sign-Up form submission (UC1.1)
   * Sends registration data to backend API
   * 
   * @param {Event} e - Form submit event
   * 
   * Process Flow:
   * 1. Prevent default form submission (stops page reload)
   * 2. Clear any previous messages
   * 3. Set loading state (disable button)
   * 4. Send POST request to /api/auth/signup
   * 5. Handle response:
   *    - Success: Show message, clear form, switch to sign-in
   *    - Error: Show error message
   * 6. Clear loading state
   * 
   * Test Coverage: TC-01 (success), TC-02 (duplicate username)
   */
  const handleSignUp = async (e) => {
    e.preventDefault();  // Prevent page reload
    setMessage({ type: '', text: '' });  // Clear previous messages
    setLoading(true);  // Show loading state

    try {
      // Send POST request to backend
      // Body contains all sign-up data as JSON
      const response = await fetch(`${API_BASE_URL}/signup`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',  // Tell server we're sending JSON
        },
        body: JSON.stringify(signUpData),  // Convert JavaScript object to JSON string
      });

      // Parse JSON response from backend
      // Expected format: { success: boolean, message: string }
      const data = await response.json();

      // Check if request was successful (HTTP 200-299)
      if (response.ok) {
        // Success: Show success message
        setMessage({ type: 'success', text: data.message });
        
        // Clear all form fields
        setSignUpData({
          username: '',
          email: '',
          password: '',
          firstName: '',
          lastName: '',
          shippingAddress: '',
        });
        
        // After 2 seconds, switch to sign-in tab
        setTimeout(() => {
          setActiveTab('signin');
        }, 2000);
      } else {
        // Error: Show error message from backend
        // Examples: "Username already exists", "Invalid email format"
        setMessage({ type: 'error', text: data.message });
      }
    } catch (error) {
      // Network error: Server unreachable or invalid response
      setMessage({ type: 'error', text: 'Network error. Please try again.' });
    } finally {
      // Always clear loading state (whether success or error)
      setLoading(false);
    }
  };

  /**
   * Handle Sign-In form submission (UC1.2)
   * Authenticates user and receives JWT token
   * 
   * @param {Event} e - Form submit event
   * 
   * Process Flow:
   * 1. Prevent default form submission
   * 2. Clear previous messages
   * 3. Set loading state
   * 4. Send POST request to /api/auth/signin
   * 5. Handle response:
   *    - Success: Store token, redirect to catalogue
   *    - Error: Show error message
   * 6. Clear loading state
   * 
   * Test Coverage: TC-03 (success), TC-04 (invalid password)
   */
  const handleSignIn = async (e) => {
    e.preventDefault();
    setMessage({ type: '', text: '' });
    setLoading(true);

    try {
      // Send POST request to backend
      const response = await fetch(`${API_BASE_URL}/signin`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(signInData),  // Send username and password
      });

      // Parse response
      // Expected format: { success: boolean, message: string, token: string, user: object }
      const data = await response.json();

      if (response.ok) {
        // Success: Show success message
        setMessage({ type: 'success', text: data.message });
        
        // Clear form
        setSignInData({ username: '', password: '' });
        
        // In a real app, you would:
        // 1. Store JWT token for future API calls
        // 2. Store user info in app state
        // 3. Redirect to catalogue page
        
        // Simulate redirect after 1.5 seconds
        setTimeout(() => {
          alert('Login successful! Redirecting to catalogue...');
          // window.location.href = '/catalogue';  // Actual redirect
        }, 1500);
      } else {
        // Error: Show error message
        // Example: "Invalid username or password"
        setMessage({ type: 'error', text: data.message });
      }
    } catch (error) {
      // Network error
      setMessage({ type: 'error', text: 'Network error. Please try again.' });
    } finally {
      setLoading(false);
    }
  };

  /**
   * Handle Forgot Password click
   * Currently shows info message - would redirect to password recovery in production
   */
  const handleForgotPassword = () => {
    setMessage({ 
      type: 'info', 
      text: 'Password reset would be sent to your email.' 
    });
    // In production: window.location.href = '/forgot-password';
  };

  // ============================================
  // STYLING HELPERS
  // ============================================
  
  /**
   * CSS classes for message container based on message type
   * Success: Green background
   * Error: Red background
   * Info: Blue background
   */
  const msgClass = {
    success: 'bg-green-50 border border-green-200',
    error: 'bg-red-50 border border-red-200',
    info: 'bg-blue-50 border border-blue-200'
  };

  /**
   * CSS classes for message text based on message type
   */
  const msgTextClass = {
    success: 'text-green-800',
    error: 'text-red-800',
    info: 'text-blue-800'
  };

  // ============================================
  // COMPONENT RENDER
  // ============================================
  
  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-600 to-purple-700 flex items-center justify-center p-4">
      {/* Main Card Container */}
      <div className="bg-white rounded-lg shadow-2xl w-full max-w-md overflow-hidden">
        
        {/* Header Section */}
        <div className="bg-gradient-to-r from-blue-600 to-purple-600 text-white p-6 text-center">
          <h1 className="text-3xl font-bold">Auction404</h1>
          <p className="text-blue-100 mt-2">Buy and Sell with Confidence</p>
        </div>

        {/* Tab Navigation */}
        <div className="flex border-b">
          {/* Sign-In Tab Button */}
          <button
            onClick={() => {
              setActiveTab('signin');
              setMessage({ type: '', text: '' });  // Clear messages when switching tabs
            }}
            className={`flex-1 py-4 text-center font-semibold transition-all ${
              activeTab === 'signin'
                ? 'bg-blue-50 text-blue-600 border-b-2 border-blue-600'  // Active tab style
                : 'text-gray-600 hover:bg-gray-50'  // Inactive tab style
            }`}
          >
            Sign In
          </button>
          
          {/* Sign-Up Tab Button */}
          <button
            onClick={() => {
              setActiveTab('signup');
              setMessage({ type: '', text: '' });
            }}
            className={`flex-1 py-4 text-center font-semibold transition-all ${
              activeTab === 'signup'
                ? 'bg-blue-50 text-blue-600 border-b-2 border-blue-600'
                : 'text-gray-600 hover:bg-gray-50'
            }`}
          >
            Sign Up
          </button>
        </div>

        {/* Form Content Area */}
        <div className="p-8">
          
          {/* Success/Error Message Display */}
          {/* Only show if message.text has content */}
          {message.text && (
            <div className={`mb-6 p-4 rounded-lg flex items-start gap-3 ${msgClass[message.type]}`}>
              {/* Icon based on message type */}
              {message.type === 'success' ? (
                <CheckCircle className="w-5 h-5 text-green-600 flex-shrink-0 mt-0.5" />
              ) : (
                <AlertCircle className="w-5 h-5 text-red-600 flex-shrink-0 mt-0.5" />
              )}
              {/* Message text */}
              <p className={`text-sm ${msgTextClass[message.type]}`}>
                {message.text}
              </p>
            </div>
          )}

          {/* 
            SIGN-IN FORM 
            Only rendered when activeTab === 'signin'
            Conditional rendering using && operator
          */}
          {activeTab === 'signin' && (
            <form onSubmit={handleSignIn} className="space-y-4">
              
              {/* Username Input Field */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Username
                </label>
                <input
                  type="text"
                  name="username"
                  value={signInData.username}  // Controlled component: React manages value
                  onChange={handleSignInChange}  // Update state on every keystroke
                  placeholder="Enter your username"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  required  // HTML5 validation: field cannot be empty
                />
              </div>

              {/* Password Input Field */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Password
                </label>
                <div className="relative">
                  {/* Input type changes based on showPassword state */}
                  <input
                    type={showPassword ? 'text' : 'password'}
                    name="password"
                    value={signInData.password}
                    onChange={handleSignInChange}
                    placeholder="Enter your password"
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    required
                  />
                  {/* Show/Hide Password Toggle Button */}
                  <button
                    type="button"  // Prevents form submission
                    onClick={() => setShowPassword(!showPassword)}  // Toggle state
                    className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-700"
                  >
                    {showPassword ? (
                      <EyeOff className="w-5 h-5" />  // Show "hide" icon when password is visible
                    ) : (
                      <Eye className="w-5 h-5" />  // Show "show" icon when password is hidden
                    )}
                  </button>
                </div>
              </div>

              {/* Forgot Password Link */}
              <button
                type="button"
                onClick={handleForgotPassword}
                className="text-sm text-blue-600 hover:text-blue-800 font-medium"
              >
                Forgot password?
              </button>

              {/* Submit Button */}
              <button
                type="submit"
                disabled={loading}  // Disable button during API call
                className="w-full bg-blue-600 hover:bg-blue-700 disabled:bg-gray-400 text-white font-semibold py-2 rounded-lg transition-all"
              >
                {/* Button text changes based on loading state */}
                {loading ? 'Signing in...' : 'Sign In'}
              </button>
            </form>
          )}

          {/* 
            SIGN-UP FORM 
            Only rendered when activeTab === 'signup'
          */}
          {activeTab === 'signup' && (
            <form onSubmit={handleSignUp} className="space-y-3">
              
              {/* Username Input */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Username
                </label>
                <input
                  type="text"
                  name="username"
                  value={signUpData.username}
                  onChange={handleSignUpChange}
                  placeholder="3-20 chars, alphanumeric"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
                  required
                />
              </div>

              {/* Email Input */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Email
                </label>
                <input
                  type="email"  // HTML5 email validation
                  name="email"
                  value={signUpData.email}
                  onChange={handleSignUpChange}
                  placeholder="your@email.com"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
                  required
                />
              </div>

              {/* First Name and Last Name (side by side) */}
              <div className="grid grid-cols-2 gap-2">
                {/* First Name */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    First Name
                  </label>
                  <input
                    type="text"
                    name="firstName"
                    value={signUpData.firstName}
                    onChange={handleSignUpChange}
                    placeholder="John"
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
                    required
                  />
                </div>

                {/* Last Name */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Last Name
                  </label>
                  <input
                    type="text"
                    name="lastName"
                    value={signUpData.lastName}
                    onChange={handleSignUpChange}
                    placeholder="Doe"
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
                    required
                  />
                </div>
              </div>

              {/* Password Input with Show/Hide Toggle */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Password
                </label>
                <div className="relative">
                  <input
                    type={showPassword ? 'text' : 'password'}
                    name="password"
                    value={signUpData.password}
                    onChange={handleSignUpChange}
                    placeholder="Min 8 chars, uppercase, lowercase, digit"
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
                    required
                  />
                  <button
                    type="button"
                    onClick={() => setShowPassword(!showPassword)}
                    className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500"
                  >
                    {showPassword ? (
                      <EyeOff className="w-4 h-4" />
                    ) : (
                      <Eye className="w-4 h-4" />
                    )}
                  </button>
                </div>
              </div>

              {/* Shipping Address Input */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Shipping Address
                </label>
                <input
                  type="text"
                  name="shippingAddress"
                  value={signUpData.shippingAddress}
                  onChange={handleSignUpChange}
                  placeholder="123 Main Street"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
                  required
                />
              </div>

              {/* Submit Button */}
              <button
                type="submit"
                disabled={loading}
                className="w-full bg-blue-600 hover:bg-blue-700 disabled:bg-gray-400 text-white font-semibold py-2 rounded-lg transition-all mt-4"
              >
                {loading ? 'Creating account...' : 'Create Account'}
              </button>

              {/* Already have an account link */}
              <p className="text-center text-sm text-gray-600 mt-4">
                Already have an account?{' '}
                <button
                  type="button"
                  onClick={() => setActiveTab('signin')}
                  className="text-blue-600 hover:text-blue-800 font-medium"
                >
                  Sign In
                </button>
              </p>
            </form>
          )}
        </div>
      </div>
    </div>
  );
}

export default AuthenticationUI;
