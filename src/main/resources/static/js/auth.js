// API Base URL
const API_BASE_URL = 'http://localhost:8080/api';

// Authentication Functions

/**
 * Login user with email and password
 * @param {string} email - User email
 * @param {string} password - User password
 * @returns {Promise<Object>} Login response data
 */
async function loginUser(email, password) {
    try {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email, password })
        });
        
        if (response.ok) {
            const data = await response.json();
            storeToken(data.token, data.email, data.role);
            return { success: true, data };
        } else {
            return { success: false, error: 'Invalid email or password' };
        }
    } catch (error) {
        console.error('Login error:', error);
        return { success: false, error: 'Failed to connect to server' };
    }
}

/**
 * Store JWT token and user info in localStorage
 * @param {string} token - JWT token
 * @param {string} email - User email
 * @param {string} role - User role
 */
function storeToken(token, email, role) {
    localStorage.setItem('token', token);
    localStorage.setItem('email', email);
    localStorage.setItem('role', role);
}

/**
 * Get stored JWT token
 * @returns {string|null} JWT token or null if not found
 */
function getToken() {
    return localStorage.getItem('token');
}

/**
 * Get stored user email
 * @returns {string|null} User email or null if not found
 */
function getUserEmail() {
    return localStorage.getItem('email');
}

/**
 * Get stored user role
 * @returns {string|null} User role or null if not found
 */
function getUserRole() {
    return localStorage.getItem('role');
}

/**
 * Make API request with Authorization header
 * @param {string} url - API endpoint URL
 * @param {Object} options - Fetch options
 * @returns {Promise<Response>} Fetch response
 */
async function apiRequest(url, options = {}) {
    const token = getToken();
    
    const headers = {
        ...options.headers,
    };
    
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    
    return fetch(url, {
        ...options,
        headers
    });
}

/**
 * Logout user by clearing token and user info
 */
function logoutUser() {
    localStorage.removeItem('token');
    localStorage.removeItem('email');
    localStorage.removeItem('role');
    window.location.href = 'index.html';
}

/**
 * Check if user is authenticated
 * @returns {boolean} True if authenticated, false otherwise
 */
function isAuthenticated() {
    return getToken() !== null;
}

/**
 * Redirect to login if not authenticated
 */
function requireAuth() {
    if (!isAuthenticated()) {
        window.location.href = 'index.html';
    }
}
