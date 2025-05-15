/**
 * CATCOSY Chatbot Initialization
 * Handles parsing product data and initializing the chatbot component
 */
document.addEventListener("DOMContentLoaded", function() {
    console.log("Initializing enhanced chatbot with product recommendations...");
    
    // Parse product information from text responses
    setupProductRecommendationParser();
    
    // Constants
    const CHAT_MESSAGES_ID = 'chatMessages';
    const PRODUCT_RECOMMENDATIONS_ID = 'productRecommendations';
    const STORAGE_KEY = 'chatbot_history';

    let chatMessages = document.getElementById(CHAT_MESSAGES_ID);
      // Function to setup product recommendation parser
    function setupProductRecommendationParser() {
        // This will be called when a new bot message is received
        if (!window.chatbotRecommendation) {
            window.chatbotRecommendation = {};
        }
        
        // Initialize the product recommendation system if not already defined
        if (!window.chatbotRecommendation.renderProductRecommendations) {
            console.log("Setting up product recommendation parser");
            
            window.chatbotRecommendation.renderProductRecommendations = function(message, messageElement) {
                // Try to extract product information from message content
                const productInfo = extractProductInformation(message.content);
                
                if (productInfo && productInfo.products && productInfo.products.length > 0) {
                    renderProductsInChatbot(productInfo.products, messageElement);
                }
            };
        }
    }
    
    /**
     * Create chatbot UI elements
     */
    function createChatbotUI() {
        // Create main container
        chatContainer = document.createElement('div');
        chatContainer.id = CHAT_CONTAINER_ID;
        chatContainer.className = 'chatbot-container';
        
        // Create header
        const header = document.createElement('div');
        header.className = 'chatbot-header';
        
        const title = document.createElement('div');
        title.className = 'chatbot-title';
        title.innerHTML = '<i class="fa fa-robot"></i> CATCOSY Hỗ Trợ';
        
        const closeBtn = document.createElement('button');
        closeBtn.className = 'chatbot-close';
        closeBtn.innerHTML = '<i class="fa fa-times"></i>';
        closeBtn.onclick = toggleChatbot;
        
        header.appendChild(title);
        header.appendChild(closeBtn);
        
        // Create messages container
        const messagesContainer = document.createElement('div');
        messagesContainer.id = CHAT_MESSAGES_ID;
        messagesContainer.className = 'chatbot-messages';
        
        // Create input area
        const inputContainer = document.createElement('div');
        inputContainer.className = 'chatbot-input-container';
        
        const input = document.createElement('input');
        input.type = 'text';
        input.id = CHAT_INPUT_ID;
        input.className = 'chatbot-input';
        input.placeholder = 'Nhập tin nhắn...';
        
        const sendBtn = document.createElement('button');
        sendBtn.id = CHAT_SEND_ID;
        sendBtn.className = 'chatbot-send';
        sendBtn.innerHTML = '<i class="fa fa-paper-plane"></i>';
        
        inputContainer.appendChild(input);
        inputContainer.appendChild(sendBtn);
        
        // Assemble all parts
        chatContainer.appendChild(header);
        chatContainer.appendChild(messagesContainer);
        chatContainer.appendChild(inputContainer);
        
        // Create toggle button
        const toggleBtn = document.createElement('button');
        toggleBtn.id = CHAT_TOGGLE_ID;
        toggleBtn.className = 'chatbot-toggle';
        toggleBtn.innerHTML = '<i class="fa fa-comments"></i>';
        toggleBtn.onclick = toggleChatbot;
        
        // Add to body
        document.body.appendChild(chatContainer);
        document.body.appendChild(toggleBtn);
        
        // Initially hide chatbot
        chatContainer.style.display = 'none';
    }
    
    /**
     * Set up event listeners for chat functionality
     */
    function setupEventListeners() {
        const sendBtn = document.getElementById(CHAT_SEND_ID);
        const input = document.getElementById(CHAT_INPUT_ID);
        
        // Send button click
        sendBtn.addEventListener('click', function() {
            sendMessage();
        });
        
        // Enter key press
        input.addEventListener('keypress', function(event) {
            if (event.key === 'Enter') {
                sendMessage();
            }
        });
    }
    
    /**
     * Send message to server
     */
    function sendMessage() {
        const input = document.getElementById(CHAT_INPUT_ID);
        const message = input.value.trim();
        
        if (message === '') return;
        
        // Clear input
        input.value = '';
        
        // Add user message to chat
        addMessageToChat('User', message);
        
        // Show typing indicator
        showTypingIndicator();
        
        // Send to server
        fetch('/chatbot/send', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ content: message })
        })
        .then(response => response.json())
        .then(data => {
            // Remove typing indicator
            removeTypingIndicator();
            
            // Add bot response
            addMessageToChat('Bot', data.content, data.context);
            
            // Save chat history
            saveChatHistory();
        })
        .catch(error => {
            console.error('Error:', error);
            removeTypingIndicator();
            addMessageToChat('Bot', 'Xin lỗi, đã xảy ra lỗi. Vui lòng thử lại sau.');
            saveChatHistory();
        });
    }
    
    /**
     * Add message to chat
     */
    function addMessageToChat(sender, content, context) {
        const messagesContainer = document.getElementById(CHAT_MESSAGES_ID);
        
        const messageDiv = document.createElement('div');
        messageDiv.className = sender === 'User' ? 'user-message' : 'bot-message';
        
        const messageContent = document.createElement('div');
        messageContent.className = 'message-content';
        messageContent.textContent = content;
        
        const messageTime = document.createElement('div');
        messageTime.className = 'message-time';
        messageTime.textContent = formatTime();
        
        messageDiv.appendChild(messageContent);
        messageDiv.appendChild(messageTime);
        messagesContainer.appendChild(messageDiv);
        
        // If there's product context and it's a bot message, render products
        if (context && sender === 'Bot' && window.chatbotRecommendation) {
            window.chatbotRecommendation.renderProductRecommendations({ content, context }, messageDiv);
        }
        
        // Scroll to bottom
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
    }
    
    /**
     * Show typing indicator
     */
    function showTypingIndicator() {
        const messagesContainer = document.getElementById(CHAT_MESSAGES_ID);
        
        const typingDiv = document.createElement('div');
        typingDiv.className = 'bot-message typing-indicator';
        typingDiv.id = 'typing-indicator';
        
        const dots = document.createElement('div');
        dots.className = 'typing-dots';
        
        for (let i = 0; i < 3; i++) {
            const dot = document.createElement('span');
            dot.className = 'dot';
            dots.appendChild(dot);
        }
        
        typingDiv.appendChild(dots);
        messagesContainer.appendChild(typingDiv);
        
        // Scroll to bottom
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
    }
    
    /**
     * Remove typing indicator
     */
    function removeTypingIndicator() {
        const indicator = document.getElementById('typing-indicator');
        if (indicator) {
            indicator.remove();
        }
    }
    
    /**
     * Format current time
     */
    function formatTime() {
        const now = new Date();
        return now.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    }
    
    /**
     * Toggle chatbot visibility
     */
    function toggleChatbot() {
        const chatContainer = document.getElementById(CHAT_CONTAINER_ID);
        const isVisible = chatContainer.style.display !== 'none';
        
        chatContainer.style.display = isVisible ? 'none' : 'flex';
        
        // Focus input when opening
        if (!isVisible) {
            setTimeout(() => {
                document.getElementById(CHAT_INPUT_ID).focus();
            }, 100);
        }
    }
    
    /**
     * Save chat history to local storage
     */
    function saveChatHistory() {
        const messagesContainer = document.getElementById(CHAT_MESSAGES_ID);
        const messages = [];
        
        // Get all message divs
        const messageDivs = messagesContainer.querySelectorAll('.user-message, .bot-message:not(.typing-indicator)');
        
        messageDivs.forEach(div => {
            const isUser = div.classList.contains('user-message');
            const content = div.querySelector('.message-content').textContent;
            
            messages.push({
                sender: isUser ? 'User' : 'Bot',
                content: content
            });
        });
        
        // Save to local storage
        localStorage.setItem(STORAGE_KEY, JSON.stringify(messages));
    }
    
    /**
     * Load chat history from local storage
     */
    function loadChatHistory() {
        try {
            const history = JSON.parse(localStorage.getItem(STORAGE_KEY));
            
            if (history && history.length > 0) {
                history.forEach(message => {
                    addMessageToChat(message.sender, message.content);
                });
            }
        } catch (e) {
            console.error('Error loading chat history:', e);
        }
    }
    
    /**
     * Show welcome message
     */
    function showWelcomeMessage() {
        const messagesContainer = document.getElementById(CHAT_MESSAGES_ID);
        
        // Only show welcome message if chat is empty
        if (messagesContainer.children.length === 0) {
            addMessageToChat('Bot', 'Xin chào! Tôi là trợ lý ảo của CATCOSY. Tôi có thể giúp gì cho bạn?');
        }
    }
    
    // Function to extract product information from message content
    function extractProductInformation(content) {
        if (!content) return null;
        
        const products = [];
        // Look for product listings in the content
        // Pattern for products: Name with price and possible discount
        const productMatches = content.match(/\*\s+\*\*([^*]+)\*\*[^0-9]+([\d.,]+)\s*VN[ĐD]/gi);
        
        // Image URL pattern
        const imageUrlPattern = /\[Xem ảnh\]\((https?:\/\/[^\s)]+)\)/gi;
        
        if (productMatches) {
            // Extract all image URLs
            const imageUrls = [];
            let match;
            while ((match = imageUrlPattern.exec(content)) !== null) {
                imageUrls.push(match[1]);
            }
            
            // Extract products
            productMatches.forEach((productMatch, index) => {
                // Get product name
                const nameMatch = productMatch.match(/\*\*([^*]+)\*\*/i);
                const name = nameMatch ? nameMatch[1].trim() : "Sản phẩm";
                
                // Get price
                const priceMatch = productMatch.match(/([\d.,]+)\s*VN[ĐD]/i);
                const price = priceMatch ? parseFloat(priceMatch[1].replace(/[.,]/g, '')) : 0;
                
                // Check for sale price
                const salePriceMatch = productMatch.match(/giảm giá chỉ còn\s+([\d.,]+)\s*VN[ĐD]/i);
                const salePrice = salePriceMatch ? parseFloat(salePriceMatch[1].replace(/[.,]/g, '')) : 0;
                
                // Assign image if available
                const imageUrl = index < imageUrls.length ? imageUrls[index] : null;
                
                products.push({
                    id: 'product_' + (Math.random().toString(36).substr(2, 9)),
                    name: name,
                    price: price,
                    salePrice: salePrice > 0 ? price - salePrice : 0,
                    imageUrls: imageUrl ? [imageUrl] : [],
                    url: '/products?keyword=' + encodeURIComponent(name)
                });
            });
        }
        
        return products.length > 0 ? { products: products } : null;
    }

    // Function to render products in chatbot
    function renderProductsInChatbot(products, messageElement) {
        if (!products || products.length === 0) return;
        
        // Create products container
        const productsContainer = document.createElement('div');
        productsContainer.className = 'chatbot-products';
        
        // Create title
        const title = document.createElement('h4');
        title.textContent = 'Sản phẩm gợi ý:';
        productsContainer.appendChild(title);
        
        // Create products slider
        const slider = document.createElement('div');
        slider.className = 'product-recommendations-slider';
        
        // Add each product
        products.forEach(product => {
            const card = createProductCard(product);
            slider.appendChild(card);
        });
        
        productsContainer.appendChild(slider);
        messageElement.appendChild(productsContainer);
        
        // Add horizontal scrolling with mouse events
        enableHorizontalScroll(slider);
    }
    
    // Create a product card
    function createProductCard(product) {
        const card = document.createElement('div');
        card.className = 'product-card';
        
        // Image container
        const imageContainer = document.createElement('div');
        imageContainer.className = 'product-image';
        
        // Product image
        const image = document.createElement('img');
        if (product.imageUrls && product.imageUrls.length > 0) {
            image.src = product.imageUrls[0];
        } else {
            image.src = '/static/img/no-image.png';
        }
        image.alt = product.name;
        image.onerror = function() {
            this.src = '/static/img/no-image.png';
        };
        
        imageContainer.appendChild(image);
        card.appendChild(imageContainer);
        
        // Product info
        const info = document.createElement('div');
        info.className = 'product-info';
        
        // Product name with link
        const nameLink = document.createElement('a');
        nameLink.href = product.url || `/products?keyword=${encodeURIComponent(product.name)}`;
        nameLink.className = 'product-name';
        nameLink.textContent = product.name;
        
        // Price container
        const priceContainer = document.createElement('div');
        priceContainer.className = 'product-price';
        
        if (product.salePrice && product.salePrice > 0) {
            // Original price
            const originalPrice = document.createElement('span');
            originalPrice.className = 'original-price';
            originalPrice.textContent = formatCurrency(product.price);
            
            // Sale price
            const salePrice = document.createElement('span');
            salePrice.className = 'sale-price';
            salePrice.textContent = formatCurrency(product.price - product.salePrice);
            
            priceContainer.appendChild(originalPrice);
            priceContainer.appendChild(salePrice);
        } else {
            // Regular price
            priceContainer.textContent = formatCurrency(product.price);
        }
        
        // Add view button
        const viewButton = document.createElement('a');
        viewButton.href = product.url || `/products?keyword=${encodeURIComponent(product.name)}`;
        viewButton.className = 'view-product';
        viewButton.textContent = 'Xem sản phẩm';
        
        // Add elements to info container
        info.appendChild(nameLink);
        info.appendChild(priceContainer);
        info.appendChild(viewButton);
        card.appendChild(info);
        
        return card;
    }
    
    // Format currency in VND
    function formatCurrency(amount) {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND',
            maximumFractionDigits: 0
        }).format(amount);
    }
    
    // Enable horizontal scrolling on product slider
    function enableHorizontalScroll(element) {
        let isDown = false;
        let startX;
        let scrollLeft;
        
        element.addEventListener('mousedown', (e) => {
            isDown = true;
            element.classList.add('active');
            startX = e.pageX - element.offsetLeft;
            scrollLeft = element.scrollLeft;
        });
        
        element.addEventListener('mouseleave', () => {
            isDown = false;
            element.classList.remove('active');
        });
        
        element.addEventListener('mouseup', () => {
            isDown = false;
            element.classList.remove('active');
        });
        
        element.addEventListener('mousemove', (e) => {
            if (!isDown) return;
            e.preventDefault();
            const x = e.pageX - element.offsetLeft;
            const walk = (x - startX) * 2; // Scroll speed
            element.scrollLeft = scrollLeft - walk;
        });
    }
    
    // Expose initialization function globally
    window.chatbotInit = {
        initRecommendations: setupProductRecommendationParser
    };
    
    // Initialize on load
    setupProductRecommendationParser();
});
