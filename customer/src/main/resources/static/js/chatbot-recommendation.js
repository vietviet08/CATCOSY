/**
 * Chatbot product recommendation handler
 * Extends the chatbot interface with product display capabilities
 */
(function() {
    'use strict';

    // Render product recommendations from chatbot response
    function renderProductRecommendations(chatMessage, messageElement) {
        // Check for product mentions in the message content
        const productsInMessage = extractProductsFromMessage(chatMessage.content);
        
        // If we have products from the structured data
        if (chatMessage.context && chatMessage.context.type === 'products' && chatMessage.context.products && chatMessage.context.products.length) {
            renderStructuredProducts(chatMessage.context.products, messageElement);
        }
        // If we found products in the message text
        else if (productsInMessage && productsInMessage.length > 0) {
            renderExtractedProducts(productsInMessage, messageElement);
        }
    }
    
    // Extract product information from message text
    function extractProductsFromMessage(messageContent) {
        if (!messageContent) return null;
        
        const products = [];
        
        // Look for product information in Markdown format or with specific patterns
        // Pattern: Product name with price and image URL
        const urlRegex = /(https?:\/\/[^\s]+\.(?:jpg|jpeg|png|gif))/gi;
        const priceRegex = /(\d{1,3}(?:[.,]\d{3})*(?:[.,]\d{0,2}))\s*VN[ĐD]/gi;
        
        // Check for typical markdown product listing format
        const markdownItems = messageContent.match(/\*\s+\*\*([^*]+)\*\*[^*]+(\d{1,3}(?:[.,]\d{3})*(?:[.,]\d{0,2}))\s*VN[ĐD].+?\[Xem ảnh\]\((https?:\/\/[^\s)]+)\)/gm);
        
        if (markdownItems) {
            markdownItems.forEach(item => {
                // Extract product name
                const nameMatch = item.match(/\*\*([^*]+)\*\*/);
                const name = nameMatch ? nameMatch[1].trim() : "Sản phẩm";
                
                // Extract price
                const priceMatch = item.match(/(\d{1,3}(?:[.,]\d{3})*(?:[.,]\d{0,2}))\s*VN[ĐD]/);
                const price = priceMatch ? parsePrice(priceMatch[1]) : 0;
                
                // Extract sale price if exists
                const salePriceMatch = item.match(/giảm giá chỉ còn\s+(\d{1,3}(?:[.,]\d{3})*(?:[.,]\d{0,2}))\s*VN[ĐD]/i);
                const salePrice = salePriceMatch ? parsePrice(salePriceMatch[1]) : 0;
                
                // Extract image URL
                const imageMatch = item.match(/\[Xem ảnh\]\((https?:\/\/[^\s)]+)\)/);
                const imageUrl = imageMatch ? imageMatch[1] : null;
                
                products.push({
                    id: generateRandomId(),
                    name: name,
                    price: price,
                    salePrice: salePrice > 0 ? price - salePrice : 0,
                    imageUrls: imageUrl ? [imageUrl] : []
                });
            });
        }
        // Fallback to simple pattern matching
        else {
            // Try to extract product info from a list format
            const listItems = messageContent.split(/[-*]\s+/);
            
            listItems.forEach(item => {
                if (item.trim() === '') return;
                
                // Try to extract price
                const priceMatch = item.match(priceRegex);
                
                if (priceMatch) {
                    const price = parsePrice(priceMatch[0].replace(/VN[ĐD]/i, '').trim());
                    const name = item.split(priceRegex)[0].trim();
                    
                    const imageMatch = item.match(urlRegex) || messageContent.match(urlRegex);
                    const imageUrl = imageMatch ? imageMatch[0] : null;
                    
                    products.push({
                        id: generateRandomId(),
                        name: name,
                        price: price,
                        salePrice: 0,
                        imageUrls: imageUrl ? [imageUrl] : []
                    });
                }
            });
        }
        
        return products.length > 0 ? products : null;
    }
    
    // Generate a random ID for products
    function generateRandomId() {
        return 'product_' + Math.random().toString(36).substr(2, 9);
    }
    
    // Parse price from string
    function parsePrice(priceStr) {
        // Remove all non-digit characters except decimal point
        const normalized = priceStr.replace(/[^\d.]/g, '');
        return parseFloat(normalized);
    }
    
    // Render products from structured data
    function renderStructuredProducts(products, messageElement) {
        renderProductsUI(products, messageElement);
    }
    
    // Render products extracted from message
    function renderExtractedProducts(products, messageElement) {
        renderProductsUI(products, messageElement);
    }
    
    // Common UI rendering for products
    function renderProductsUI(products, messageElement) {
        // Create products container
        const productsContainer = document.createElement('div');
        productsContainer.className = 'chatbot-products';
        
        // Add title
        const productsTitle = document.createElement('h4');
        productsTitle.textContent = 'Sản phẩm gợi ý cho bạn:';
        productsContainer.appendChild(productsTitle);
        
        // Create carousel/slider for products
        const productsWrapper = document.createElement('div');
        productsWrapper.className = 'product-recommendations-slider';
        
        // Add products
        products.forEach(product => {
            const productCard = createProductCard(product);
            productsWrapper.appendChild(productCard);
        });
        
        productsContainer.appendChild(productsWrapper);
        
        // Append to message
        messageElement.appendChild(productsContainer);
        
        // Initialize slider if needed
        initProductSlider(productsWrapper);
    }
    
    // Create a product card element
    function createProductCard(product) {
        const card = document.createElement('div');
        card.className = 'product-card';
        card.dataset.productId = product.id;
        
        // Product image
        const imageContainer = document.createElement('div');
        imageContainer.className = 'product-image';
        
        const image = document.createElement('img');
        image.src = product.imageUrls && product.imageUrls.length > 0 ? product.imageUrls[0] : '/static/img/no-image.png';
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
        nameLink.href = `/product-detail/${product.id}`;
        nameLink.className = 'product-name';
        nameLink.textContent = product.name;
        
        // Price information
        const priceContainer = document.createElement('div');
        priceContainer.className = 'product-price';
        
        if (product.salePrice && product.salePrice > 0) {
            const originalPrice = document.createElement('span');
            originalPrice.className = 'original-price';
            originalPrice.textContent = formatCurrency(product.price);
            
            const salePrice = document.createElement('span');
            salePrice.className = 'sale-price';
            salePrice.textContent = formatCurrency(product.price - product.salePrice);
            
            priceContainer.appendChild(originalPrice);
            priceContainer.appendChild(salePrice);
        } else {
            priceContainer.textContent = formatCurrency(product.price);
        }
        
        // Category/Brand if available
        let categoryBrand = '';
        if (product.category && product.brand) {
            categoryBrand = `${product.category} | ${product.brand}`;
        } else if (product.category) {
            categoryBrand = product.category;
        } else if (product.brand) {
            categoryBrand = product.brand;
        }
        
        const categoryBrandElement = document.createElement('div');
        categoryBrandElement.className = 'product-category-brand';
        categoryBrandElement.textContent = categoryBrand;
        
        // Add all elements to info container
        info.appendChild(nameLink);
        info.appendChild(priceContainer);
        info.appendChild(categoryBrandElement);
        
        // Add view button
        const viewButton = document.createElement('a');
        viewButton.href = `/product-detail/${product.id}`;
        viewButton.className = 'btn btn-sm btn-primary view-product';
        viewButton.textContent = 'Xem sản phẩm';
        
        info.appendChild(viewButton);
        card.appendChild(info);
        
        return card;
    }
    
    // Format currency (VND)
    function formatCurrency(amount) {
        return new Intl.NumberFormat('vi-VN', { 
            style: 'currency', 
            currency: 'VND',
            maximumFractionDigits: 0
        }).format(amount);
    }
      // Initialize product slider
    function initProductSlider(containerElement) {
        // Add drag scrolling functionality for mobile and desktop
        let isDown = false;
        let startX;
        let scrollLeft;

        containerElement.addEventListener('mousedown', (e) => {
            isDown = true;
            containerElement.classList.add('active');
            startX = e.pageX - containerElement.offsetLeft;
            scrollLeft = containerElement.scrollLeft;
        });
        
        containerElement.addEventListener('mouseleave', () => {
            isDown = false;
            containerElement.classList.remove('active');
        });
        
        containerElement.addEventListener('mouseup', () => {
            isDown = false;
            containerElement.classList.remove('active');
        });
        
        containerElement.addEventListener('mousemove', (e) => {
            if (!isDown) return;
            e.preventDefault();
            const x = e.pageX - containerElement.offsetLeft;
            const walk = (x - startX) * 2; // Scroll speed
            containerElement.scrollLeft = scrollLeft - walk;
        });
        
        // If page has jQuery and Slick slider, initialize it
        if (window.jQuery && typeof jQuery.fn.slick === 'function') {
            try {
                $(containerElement).slick({
                    dots: false,
                    infinite: false,
                    speed: 300,
                    slidesToShow: 2,
                    slidesToScroll: 1,
                    responsive: [
                        {
                            breakpoint: 768,
                            settings: {
                                slidesToShow: 1
                            }
                        }
                    ]
                });
            } catch(err) {
                console.log('Slick slider not available, using default scrolling');
            }
        }
    }
    
    // Export functions to global scope
    window.chatbotRecommendation = {
        renderProductRecommendations: renderProductRecommendations
    };
})();
