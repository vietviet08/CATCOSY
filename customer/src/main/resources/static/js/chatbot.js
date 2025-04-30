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
    
    // Generate a unique user ID for this session
    const userId = 'user-' + Math.random().toString(36).substring(2, 10);
    console.log("Generated User ID:", userId);
    
    // Conversation history
    let conversationHistory = [];
    
    // Function to toggle chatbot visibility
    function toggleChatbot() {
        console.log("Toggle chatbot called");
        chatbotBox.classList.toggle('active');
        console.log("Chatbox is active:", chatbotBox.classList.contains('active'));
        
        // If opening for the first time, show welcome message
        if (chatbotBox.classList.contains('active') && chatbotMessages.children.length === 0) {
            // Try to load past messages from local storage
            try {
                const savedHistory = localStorage.getItem('chatbotHistory');
                if (savedHistory) {
                    conversationHistory = JSON.parse(savedHistory);
                    
                    // Restore previous conversation if it exists
                    if (conversationHistory.length > 0) {
                        conversationHistory.forEach(msg => {
                            if (msg.role === 'user') {
                                const messageElement = document.createElement('div');
                                messageElement.classList.add('message', 'user-message');
                                messageElement.textContent = msg.content;
                                chatbotMessages.appendChild(messageElement);
                            } else if (msg.role === 'assistant') {
                                const messageElement = document.createElement('div');
                                messageElement.classList.add('message', 'bot-message');
                                messageElement.textContent = msg.content;
                                chatbotMessages.appendChild(messageElement);
                            }
                        });
                        
                        // Add a continuation message
                        showBotMessage("Welcome back! How can I help you today?");
                    } else {
                        // Show new welcome message
                        showBotMessage("👋 Welcome to CATCOSY! How can I assist you today?");
                    }
                } else {
                    // Show new welcome message if no history
                    showBotMessage("👋 Welcome to CATCOSY! How can I assist you today?");
                }
            } catch (e) {
                console.error("Error loading chat history:", e);
                showBotMessage("👋 Welcome to CATCOSY! How can I assist you today?");
            }
            
            showSuggestions(initialSuggestions);
            
            // Scroll to bottom of conversation
            chatbotMessages.scrollTop = chatbotMessages.scrollHeight;
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
            addToHistory('assistant', text);
            
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
        addToHistory('user', text);
        
        // Scroll to bottom
        chatbotMessages.scrollTop = chatbotMessages.scrollHeight;
    }
    
    // Function to add message to history
    function addToHistory(role, content) {
        conversationHistory.push({ role, content });
        
        // Save to local storage
        try {
            localStorage.setItem('chatbotHistory', JSON.stringify(conversationHistory));
        } catch (e) {
            console.error("Error saving chat history:", e);
        }
    }
    
    // Function to clear history
    function clearHistory() {
        conversationHistory = [];
        try {
            localStorage.removeItem('chatbotHistory');
        } catch (e) {
            console.error("Error clearing chat history:", e);
        }
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
        
        // Show typing indicator
        chatbotTyping.style.display = 'flex';
        
        // Prepare the payload
        const payload = {
            message: text,
            userId: userId,
            conversationHistory: conversationHistory
        };
        
        // Send to backend for processing
        fetch('/api/chatbot/send', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify(payload),
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok: ' + response.status);
            }
            return response.json();
        })
        .then(data => {
            // Hide typing indicator
            chatbotTyping.style.display = 'none';
            
            // Display bot response
            if (data.message) {
                showBotMessage(data.message);
            }
            
            // Show suggestions based on the context
            showSuggestions(getContextualSuggestions(text));
        })
        .catch(error => {
            console.error('Error:', error);
            
            // Hide typing indicator
            chatbotTyping.style.display = 'none';
            
            // Try to use local fallback responses
            const fallbackResponse = getLocalFallbackResponse(text);
            showBotMessage(fallbackResponse);
            
            showSuggestions(initialSuggestions);
        });
    }
    
    // Function to generate a local fallback response when API is unavailable
    function getLocalFallbackResponse(text) {
        text = text.toLowerCase();
        
        if (text.includes('hi') || text.includes('hello') || text.includes('chào')) {
            return "Xin chào! Tôi là trợ lý ảo của CATCOSY. Rất vui được hỗ trợ bạn hôm nay. Tôi có thể giúp gì cho bạn?";
        } 
        else if (text.includes('product') || text.includes('clothing') || text.includes('sản phẩm') || text.includes('quần áo')) {
            return "CATCOSY cung cấp nhiều sản phẩm thời trang chất lượng cao. Bạn có thể tìm thấy áo thun, quần jeans, váy, và nhiều loại phụ kiện khác trên website của chúng tôi.";
        }
        else if (text.includes('shipping') || text.includes('delivery') || text.includes('giao hàng')) {
            return "CATCOSY cung cấp dịch vụ giao hàng nhanh trong 2-3 ngày làm việc cho đơn hàng nội thành và 3-5 ngày cho đơn hàng toàn quốc.";
        }
        else if (text.includes('return') || text.includes('refund') || text.includes('đổi trả') || text.includes('hoàn tiền')) {
            return "Bạn có thể đổi trả sản phẩm trong vòng 14 ngày kể từ ngày nhận hàng. Sản phẩm cần còn nguyên tem mác và chưa qua sử dụng.";
        }
        else if (text.includes('contact') || text.includes('liên hệ')) {
            return "Bạn có thể liên hệ với CATCOSY qua số điện thoại 1900-1234 hoặc email support@catcosy.com.";
        }
        else {
            return "Cảm ơn bạn đã liên hệ với CATCOSY! Hiện tại máy chủ đang bận, tôi không thể xử lý yêu cầu của bạn. Vui lòng thử lại sau hoặc liên hệ với chúng tôi qua số điện thoại 1900-1234.";
        }
    }
    
    // Function to generate contextual suggestions based on the current conversation
    function getContextualSuggestions(text) {
        text = text.toLowerCase();
        
        // Default suggestions
        let suggestions = [...initialSuggestions];
        
        // Contextual suggestions based on keywords
        if (text.includes('product') || text.includes('clothing') || text.includes('clothes') || text.includes('shop') || 
            text.includes('sản phẩm') || text.includes('quần áo')) {
            suggestions = ["Xem sản phẩm mới", "Danh mục sản phẩm", "Sản phẩm bán chạy", "Khuyến mãi", "Hướng dẫn kích cỡ"];
        } 
        else if (text.includes('shipping') || text.includes('delivery') || text.includes('track') || 
                text.includes('giao hàng') || text.includes('vận chuyển')) {
            suggestions = ["Phí giao hàng", "Thời gian giao hàng", "Theo dõi đơn hàng", "Giao hàng quốc tế", "Chính sách giao hàng"];
        } 
        else if (text.includes('return') || text.includes('refund') || text.includes('exchange') || 
                text.includes('đổi trả') || text.includes('hoàn tiền')) {
            suggestions = ["Chính sách đổi trả", "Cách thức đổi trả", "Quy trình hoàn tiền", "Đổi sản phẩm", "Liên hệ hỗ trợ"];
        } 
        else if (text.includes('account') || text.includes('login') || text.includes('register') || text.includes('profile') ||
                text.includes('tài khoản') || text.includes('đăng nhập') || text.includes('đăng ký')) {
            suggestions = ["Tạo tài khoản", "Vấn đề đăng nhập", "Cập nhật hồ sơ", "Xem đơn hàng", "Đặt lại mật khẩu"];
        } 
        else if (text.includes('payment') || text.includes('pay') || text.includes('card') || text.includes('checkout') ||
                text.includes('thanh toán') || text.includes('thẻ')) {
            suggestions = ["Phương thức thanh toán", "Vấn đề thanh toán", "Bảo mật thanh toán", "Thêm phương thức thanh toán", "Mã giảm giá"];
        }
        
        return suggestions;
    }
    
    // Add a clear history button
    const addClearHistoryButton = () => {
        // Check if button already exists
        if (document.getElementById('chatbot-clear-history')) {
            return;
        }
        
        // Create the button
        const clearButton = document.createElement('button');
        clearButton.id = 'chatbot-clear-history';
        clearButton.innerHTML = '<i class="fa-solid fa-trash"></i>';
        clearButton.title = 'Clear conversation history';
        clearButton.className = 'chatbot-clear-button';
        
        // Style the button
        clearButton.style.position = 'absolute';
        clearButton.style.top = '12px';
        clearButton.style.right = '40px';
        clearButton.style.background = 'none';
        clearButton.style.border = 'none';
        clearButton.style.color = '#fff';
        clearButton.style.cursor = 'pointer';
        clearButton.style.fontSize = '14px';
        
        // Add click event
        clearButton.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            
            // Clear the chat display
            chatbotMessages.innerHTML = '';
            
            // Clear the history
            clearHistory();
            
            // Show welcome message
            showBotMessage("I've cleared our conversation. How can I help you today?");
            showSuggestions(initialSuggestions);
        });
        
        // Add to the header
        const header = document.querySelector('.chatbot-header');
        if (header) {
            header.appendChild(clearButton);
        }
    };
    
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
    
    // Add the clear history button
    addClearHistoryButton();
    
    // Add direct global access for debugging
    window.toggleChatbot = toggleChatbot;
    window.debugChatbot = {
        button: chatbotButton,
        box: chatbotBox,
        isVisible: function() {
            return chatbotBox ? chatbotBox.classList.contains('active') : false;
        },
        toggle: toggleChatbot,
        clearHistory: function() {
            clearHistory();
            chatbotMessages.innerHTML = '';
            showBotMessage("History cleared.");
        }
    };
    
    console.log("Chatbot initialization complete");
});