/**
 * CATCOSY Chatbot History Manager
 * This script manages the persistence of conversation history for the chatbot
 */

class ChatbotHistoryManager {
    constructor(maxHistoryLength = 10) {
        this.storageKey = 'catcosy_chatbot_history';
        this.maxHistoryLength = maxHistoryLength;
        this.history = this.loadHistory();
    }
    
    /**
     * Load conversation history from local storage
     */
    loadHistory() {
        try {
            const savedHistory = localStorage.getItem(this.storageKey);
            return savedHistory ? JSON.parse(savedHistory) : [];
        } catch (error) {
            console.error('Error loading chatbot history:', error);
            return [];
        }
    }
    
    /**
     * Save conversation history to local storage
     */
    saveHistory() {
        try {
            localStorage.setItem(this.storageKey, JSON.stringify(this.history));
        } catch (error) {
            console.error('Error saving chatbot history:', error);
        }
    }
    
    /**
     * Add a message to the conversation history
     * @param {string} role - 'user' or 'assistant'
     * @param {string} content - The message content
     */
    addMessage(role, content) {
        // Add message to history
        this.history.push({ role, content });
        
        // Trim history if it exceeds the maximum length
        if (this.history.length > this.maxHistoryLength) {
            this.history = this.history.slice(this.history.length - this.maxHistoryLength);
        }
        
        // Save updated history
        this.saveHistory();
    }
    
    /**
     * Get the current conversation history
     * @returns {Array} The conversation history
     */
    getHistory() {
        return [...this.history];
    }
    
    /**
     * Clear the conversation history
     */
    clearHistory() {
        this.history = [];
        this.saveHistory();
    }
    
    /**
     * Get the most recent user query
     * @returns {string|null} The most recent user query or null if none exists
     */
    getLastUserQuery() {
        for (let i = this.history.length - 1; i >= 0; i--) {
            if (this.history[i].role === 'user') {
                return this.history[i].content;
            }
        }
        return null;
    }
    
    /**
     * Get the most recent assistant response
     * @returns {string|null} The most recent assistant response or null if none exists
     */
    getLastAssistantResponse() {
        for (let i = this.history.length - 1; i >= 0; i--) {
            if (this.history[i].role === 'assistant') {
                return this.history[i].content;
            }
        }
        return null;
    }
}

// Make accessible globally
window.chatbotHistory = new ChatbotHistoryManager();