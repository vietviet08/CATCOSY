/**
 * CATCOSY Chatbot
 * File JS để gọi từ chatbot controller
 * Xử lý giao diện và kết nối với API của chatbot
 */

// Khởi tạo chatbot khi tài liệu đã tải xong
document.addEventListener('DOMContentLoaded', function() {
    console.log("Chatbot script loaded");

    // Lấy các phần tử DOM cần thiết
    const chatbotButton = document.querySelector('.chatbot-button');
    const chatbotBox = document.querySelector('.chatbot-box');
    const chatbotClose = document.querySelector('.close-btn');
    const clearButton = document.getElementById('clearChat');
    const messageInput = document.getElementById('messageInput');
    const sendButton = document.getElementById('sendButton');
    const chatMessages = document.getElementById('chatMessages');
    const typingIndicator = document.getElementById('typingIndicator');
    
    console.log("Chatbot elements:", {
        button: chatbotButton, 
        box: chatbotBox, 
        close: chatbotClose,
        clear: clearButton
    });
    
    // Xử lý sự kiện click vào nút chatbot
    if (chatbotButton) {
        // Thêm sự kiện click thông qua trình nghe trực tiếp
        chatbotButton.onclick = function(e) {
            e.preventDefault();
            e.stopPropagation();
            console.log("Button clicked");
            if (chatbotBox) {
                chatbotBox.classList.toggle('active');
                console.log("Chatbot visibility toggled:", chatbotBox.classList.contains('active'));
            }
        };
    } else {
        console.error("Chatbot button not found in DOM");
    }
    
    // Đóng chatbox khi click vào nút đóng
    if (chatbotClose) {
        chatbotClose.onclick = function(e) {
            e.preventDefault();
            e.stopPropagation();
            console.log("Close button clicked");
            if (chatbotBox) {
                chatbotBox.classList.remove('active');
            }
        };
    }
    
    // Xử lý click cho các chip đề xuất
    document.querySelectorAll('.suggestion-chip').forEach(chip => {
        chip.onclick = function() {
            if (messageInput) {
                messageInput.value = this.textContent;
                messageInput.focus();
            }
        };
    });
    
    // Hiển thị thời gian
    function formatTime() {
        const now = new Date();
        const hours = now.getHours().toString().padStart(2, '0');
        const minutes = now.getMinutes().toString().padStart(2, '0');
        return `Hôm nay, lúc ${hours}:${minutes}`;
    }
    
    // Thêm tin nhắn vào giao diện
    function addMessage(content, isUser, source = null) {
        if (!chatMessages) return;
        
        const messageDiv = document.createElement('div');
        messageDiv.className = `message ${isUser ? 'user-message' : 'bot-message'}`;
        
        messageDiv.innerHTML = `
            ${content}
            <div class="message-time">${formatTime()}</div>
            ${source ? `<div class="source-indicator">Nguồn: ${source}</div>` : ''}
        `;
        
        chatMessages.appendChild(messageDiv);
        chatMessages.scrollTop = chatMessages.scrollHeight;
    }
    
    // Hiển thị chỉ báo đang nhập
    function showTypingIndicator() {
        if (!chatMessages) return;
        
        const indicator = document.createElement('div');
        indicator.id = 'typingIndicator';
        indicator.className = 'typing-indicator';
        indicator.innerHTML = `
            <div class="dot"></div>
            <div class="dot"></div>
            <div class="dot"></div>
        `;
        
        chatMessages.appendChild(indicator);
        chatMessages.scrollTop = chatMessages.scrollHeight;
    }
    
    // Ẩn chỉ báo đang nhập
    function hideTypingIndicator() {
        const indicator = document.getElementById('typingIndicator');
        if (indicator) {
            indicator.remove();
        }
    }
    
    // Gửi tin nhắn đến API và xử lý phản hồi
    async function sendMessage(message) {
        try {
            console.log("Sending message to API...");
            // Hiển thị chỉ báo đang nhập
            showTypingIndicator();
            
            // Vô hiệu hóa nút gửi
            if (sendButton) sendButton.disabled = true;
            
            // Gọi API từ ChatbotController
            const response = await fetch('/chatbot/send', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ sender: 'User', content: message })
            });
            
            console.log("Response status:", response.status);
            
            if (response.ok) {
                // Nhận phản hồi từ API
                const botMessage = await response.json();
                console.log("Bot response:", botMessage);
                
                // Ẩn chỉ báo đang nhập
                hideTypingIndicator();
                
                // Xử lý và hiển thị nội dung phản hồi
                if (botMessage && botMessage.content) {
                    try {
                        // Kiểm tra xem nội dung có phải là chuỗi JSON không
                        if (botMessage.content.trim().startsWith('{') || botMessage.content.trim().startsWith('[')) {
                            const jsonData = JSON.parse(botMessage.content);
                            // Hiển thị dữ liệu JSON theo định dạng đẹp
                            addMessage(formatJsonResponse(jsonData), false);
                        } else {
                            // Hiển thị phản hồi thông thường
                            addMessage(botMessage.content, false);
                        }
                    } catch (e) {
                        // Nếu không phải JSON, hiển thị nội dung gốc
                        addMessage(botMessage.content, false);
                    }
                }
            } else {
                console.error('Error status:', response.status);
                hideTypingIndicator();
                
                try {
                    const errorText = await response.text();
                    console.error('Error response:', errorText);
                } catch (e) {
                    console.error('Could not read error response');
                }
                
                addMessage('Đã có lỗi xảy ra. Vui lòng thử lại sau. (Mã lỗi: ' + response.status + ')', false, 'error');
            }
        } catch (error) {
            console.error('Network Error:', error);
            hideTypingIndicator();
            addMessage('Không thể kết nối đến máy chủ. Vui lòng kiểm tra kết nối mạng và thử lại.', false, 'error');
        } finally {
            if (sendButton) sendButton.disabled = false;
        }
    }
    
    // Format JSON response để hiển thị đẹp hơn
    function formatJsonResponse(jsonData) {
        // Tạo HTML từ dữ liệu JSON
        let formattedContent = '';
        
        if (Array.isArray(jsonData)) {
            // Xử lý mảng
            jsonData.forEach((item, index) => {
                if (typeof item === 'object') {
                    formattedContent += `<div class="json-item"><strong>Mục ${index + 1}:</strong><br>`;
                    Object.keys(item).forEach(key => {
                        formattedContent += `<span class="json-key">${key}:</span> <span class="json-value">${item[key]}</span><br>`;
                    });
                    formattedContent += `</div>`;
                } else {
                    formattedContent += `<div class="json-item">${item}</div>`;
                }
            });
        } else if (typeof jsonData === 'object') {
            // Xử lý object
            Object.keys(jsonData).forEach(key => {
                if (typeof jsonData[key] === 'object') {
                    formattedContent += `<div class="json-item"><span class="json-key">${key}:</span><br>`;
                    Object.keys(jsonData[key]).forEach(subKey => {
                        formattedContent += `&nbsp;&nbsp;<span class="json-subkey">${subKey}:</span> <span class="json-value">${jsonData[key][subKey]}</span><br>`;
                    });
                    formattedContent += `</div>`;
                } else {
                    formattedContent += `<div class="json-item"><span class="json-key">${key}:</span> <span class="json-value">${jsonData[key]}</span></div>`;
                }
            });
        } else {
            // Xử lý giá trị đơn giản
            formattedContent = jsonData.toString();
        }
        
        return formattedContent;
    }
    
    // Xử lý sự kiện gửi tin nhắn
    function handleSend() {
        if (!messageInput) return;
        
        const message = messageInput.value.trim();
        if (message) {
            // Thêm tin nhắn của người dùng vào giao diện
            addMessage(message, true);
            
            // Gửi tin nhắn đến API
            sendMessage(message);
            
            // Xóa nội dung input
            messageInput.value = '';
        }
    }
    
    // Xử lý sự kiện xóa cuộc trò chuyện
    function handleClear() {
        if (!chatMessages) return;
        
        // Xóa tin nhắn trên giao diện
        chatMessages.innerHTML = '';
        
        // Thêm tin nhắn chào mừng
        addMessage('Xin chào! Tôi là trợ lý ảo của CATCOSY. Tôi có thể giúp gì cho bạn về các sản phẩm thời trang, xu hướng, hoặc giải đáp thắc mắc của bạn?', false);
    }
    
    // Đăng ký sự kiện cho nút gửi
    if (sendButton) {
        sendButton.onclick = handleSend;
    }
    
    // Đăng ký sự kiện Enter cho input
    if (messageInput) {
        messageInput.onkeypress = function(event) {
            if (event.key === 'Enter') {
                handleSend();
                event.preventDefault();
            }
        };
    }
    
    // Đăng ký sự kiện cho nút xóa cuộc trò chuyện
    if (clearButton) {
        clearButton.onclick = handleClear;
    }
    
    // Đặt thời gian cho tin nhắn chào mừng
    const welcomeTimeDiv = document.querySelector('.bot-message .message-time');
    if (welcomeTimeDiv) {
        welcomeTimeDiv.textContent = formatTime();
    }
});