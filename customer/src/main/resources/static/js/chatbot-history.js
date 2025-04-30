// CATCOSY Chatbot History Manager

/**
 * Chatbot History Manager
 * Handles storing and retrieving chat history from localStorage
 */
const ChatbotHistory = (function() {
    const STORAGE_KEY = 'catcosy_chatbot_history';
    const MAX_MESSAGES = 50; // Maximum number of messages to store
    
    // Get chat history from localStorage
    function getHistory() {
        try {
            const data = localStorage.getItem(STORAGE_KEY);
            return data ? JSON.parse(data) : [];
        } catch (error) {
            console.error('Error loading chat history:', error);
            return [];
        }
    }
    
    // Save chat history to localStorage
    function saveHistory(history) {
        try {
            // Limit the number of messages stored
            const limitedHistory = history.slice(-MAX_MESSAGES);
            localStorage.setItem(STORAGE_KEY, JSON.stringify(limitedHistory));
            return true;
        } catch (error) {
            console.error('Error saving chat history:', error);
            return false;
        }
    }
    
    // Add a message to history
    function addMessage(role, content) {
        const history = getHistory();
        const timestamp = new Date().toISOString();
        
        history.push({
            role,
            content,
            timestamp
        });
        
        return saveHistory(history);
    }
    
    // Clear chat history
    function clearHistory() {
        try {
            localStorage.removeItem(STORAGE_KEY);
            return true;
        } catch (error) {
            console.error('Error clearing chat history:', error);
            return false;
        }
    }
    
    // Get chat history formatted for API
    function getFormattedHistory() {
        const history = getHistory();
        return history.map(msg => ({
            role: msg.role,
            content: msg.content
        }));
    }
    
    // Return public API
    return {
        getHistory,
        saveHistory,
        addMessage,
        clearHistory,
        getFormattedHistory
    };
})();

// Make available globally
window.ChatbotHistory = ChatbotHistory;