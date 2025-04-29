// CATCOSY Chatbot Implementation

document.addEventListener('DOMContentLoaded', function() {
    console.log("DOM fully loaded - initializing chatbot");
    
    // Get DOM elements
    const chatbotButton = document.getElementById('chatbotButton');
    const chatbotBox = document.getElementById('chatbotBox');
    const chatbotClose = document.getElementById('chatbotClose');
    const chatbotMessages = document.getElementById('chatbotMessages');
    const chatbotInput = document.getElementById('chatbotInput');
    const chatbotSend = document.getElementById('chatbotSend');
    const chatbotTyping = document.getElementById('chatbotTyping');
    const suggestionChips = document.getElementById('suggestionChips');
    
    // Debug elements
    console.log("chatbotButton:", chatbotButton);
    console.log("chatbotBox:", chatbotBox);
    
    // Initial suggestions
    const initialSuggestions = [
        "Products",
        "Shipping",
        "Returns",
        "Account",
        "Payment methods"
    ];
    
    // Store conversation history
    let conversationHistory = [];
    
    // Function to toggle chatbot visibility
    function toggleChatbot() {
        console.log("Toggle chatbot called");
        chatbotBox.classList.toggle('active');
        console.log("Chatbox is active:", chatbotBox.classList.contains('active'));
        
        // If opening for the first time, show welcome message
        if (chatbotBox.classList.contains('active') && chatbotMessages.children.length === 0) {
            showBotMessage("👋 Welcome to CATCOSY! How can I assist you today?");
            showSuggestions(initialSuggestions);
        }
    }
    
    // Function to close the chatbot
    function closeChatbot() {
        console.log("Close chatbot called");
        chatbotBox.classList.remove('active');
    }
    
    // Function to add a bot message
    function showBotMessage(text) {
        // Show typing indicator
        chatbotTyping.style.display = 'flex';
        chatbotMessages.scrollTop = chatbotMessages.scrollHeight;
        
        // Add slight delay to simulate typing
        setTimeout(() => {
            // Hide typing indicator
            chatbotTyping.style.display = 'none';
            
            // Create message element
            const messageElement = document.createElement('div');
            messageElement.classList.add('message', 'bot-message');
            
            // Handle multi-line responses with lists
            if (text.includes('•') || text.includes('*')) {
                const parts = text.split(/[•*]/);
                const mainText = parts.shift();
                
                messageElement.textContent = mainText;
                
                if (parts.length > 0) {
                    const list = document.createElement('ul');
                    list.style.paddingLeft = '20px';
                    list.style.marginTop = '5px';
                    list.style.marginBottom = '0';
                    
                    parts.forEach(item => {
                        if (item.trim()) {
                            const listItem = document.createElement('li');
                            listItem.textContent = item.trim();
                            list.appendChild(listItem);
                        }
                    });
                    
                    messageElement.appendChild(list);
                }
            } else {
                messageElement.textContent = text;
            }
            
            // Add message to chat
            chatbotMessages.appendChild(messageElement);
            
            // Save to conversation history
            conversationHistory.push({ role: 'assistant', content: text });
            
            // Scroll to bottom
            chatbotMessages.scrollTop = chatbotMessages.scrollHeight;
        }, 500); // 0.5s delay for typing effect
    }
    
    // Function to add a user message
    function showUserMessage(text) {
        // Create message element
        const messageElement = document.createElement('div');
        messageElement.classList.add('message', 'user-message');
        messageElement.textContent = text;
        
        // Add message to chat
        chatbotMessages.appendChild(messageElement);
        
        // Save to conversation history
        conversationHistory.push({ role: 'user', content: text });
        
        // Scroll to bottom
        chatbotMessages.scrollTop = chatbotMessages.scrollHeight;
    }
    
    // Function to show suggestion chips
    function showSuggestions(suggestions) {
        // Clear existing suggestions
        suggestionChips.innerHTML = '';
        
        // Add suggestion chips
        suggestions.forEach(suggestion => {
            const chip = document.createElement('div');
            chip.classList.add('suggestion-chip');
            chip.textContent = suggestion;
            
            chip.addEventListener('click', function() {
                handleUserInput(suggestion);
            });
            
            suggestionChips.appendChild(chip);
        });
    }
    
    // Function to handle sending a message
    function sendMessage() {
        const text = chatbotInput.value.trim();
        
        if (text) {
            // Clear input
            chatbotInput.value = '';
            
            // Show user message
            showUserMessage(text);
            
            // Clear suggestions when user sends a custom message
            suggestionChips.innerHTML = '';
            
            // Handle the user message and generate a response
            handleUserInput(text);
        }
    }
    
    // Function to handle user input and generate appropriate responses
    function handleUserInput(text) {
        // If it's a suggestion chip that was clicked, show as user message
        if (!chatbotInput.value && text) {
            showUserMessage(text);
        }
        
        // Send to backend for processing
        fetch('/api/chatbot', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                message: text,
                history: conversationHistory
            }),
        })
        .then(response => response.json())
        .then(data => {
            // Display bot response
            showBotMessage(data.response);
            
            // Show suggestions if provided by the backend
            if (data.suggestions && data.suggestions.length > 0) {
                showSuggestions(data.suggestions);
            } else {
                // Show default suggestions if none from backend
                showSuggestions(getContextualSuggestions(text));
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showBotMessage("I'm sorry, I'm having trouble connecting. Please try again later.");
            showSuggestions(initialSuggestions);
        });
    }
    
    // Function to generate contextual suggestions based on the current conversation
    function getContextualSuggestions(text) {
        text = text.toLowerCase();
        
        // Default suggestions
        let suggestions = [...initialSuggestions];
        
        // Contextual suggestions based on keywords
        if (text.includes('product') || text.includes('clothing') || text.includes('clothes') || text.includes('shop')) {
            suggestions = ["View new arrivals", "Product categories", "Bestsellers", "Sales & promotions", "Size guide"];
        } 
        else if (text.includes('shipping') || text.includes('delivery') || text.includes('track')) {
            suggestions = ["Shipping costs", "Delivery times", "Track my order", "International shipping", "Shipping policy"];
        } 
        else if (text.includes('return') || text.includes('refund') || text.includes('exchange')) {
            suggestions = ["Return policy", "How to return", "Refund process", "Exchange items", "Contact support"];
        } 
        else if (text.includes('account') || text.includes('login') || text.includes('register') || text.includes('profile')) {
            suggestions = ["Create account", "Login issues", "Update profile", "View orders", "Reset password"];
        } 
        else if (text.includes('payment') || text.includes('pay') || text.includes('card') || text.includes('checkout')) {
            suggestions = ["Payment methods", "Checkout issues", "Payment security", "Add payment method", "Vouchers"];
        }
        
        return suggestions;
    }
    
    // Event listeners
    if (chatbotButton) {
        console.log("Adding click event listener to chatbot button");
        
        // Using multiple event binding methods for redundancy
        chatbotButton.addEventListener('click', function(e) {
            console.log("Chatbot button clicked!");
            e.preventDefault();
            e.stopPropagation();
            toggleChatbot();
        });
        
        // Also try with onclick property
        chatbotButton.onclick = function(e) {
            console.log("Chatbot button onclick fired!");
            e.preventDefault();
            e.stopPropagation();
            toggleChatbot();
            return false;
        };
    } else {
        console.error("Chatbot button element not found in DOM");
    }
    
    if (chatbotClose) {
        chatbotClose.addEventListener('click', closeChatbot);
    } else {
        console.error("Chatbot close button not found in DOM");
    }
    
    if (chatbotSend) {
        chatbotSend.addEventListener('click', sendMessage);
    } else {
        console.error("Chatbot send button not found in DOM");
    }
    
    if (chatbotInput) {
        chatbotInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                sendMessage();
            }
        });
    } else {
        console.error("Chatbot input not found in DOM");
    }
    
    // Focus input when chat is opened
    if (chatbotButton && chatbotInput) {
        chatbotButton.addEventListener('click', function() {
            if (chatbotBox.classList.contains('active')) {
                setTimeout(() => chatbotInput.focus(), 300);
            }
        });
    }
    
    // Add direct global access for debugging
    window.toggleChatbot = toggleChatbot;
    window.debugChatbot = {
        button: chatbotButton,
        box: chatbotBox,
        isVisible: function() {
            return chatbotBox ? chatbotBox.classList.contains('active') : false;
        },
        toggle: toggleChatbot
    };
    
    console.log("Chatbot initialization complete");
});