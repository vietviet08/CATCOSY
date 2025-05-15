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
    }    // Common UI rendering for products
    function renderProductsUI(products, messageElement) {
        // Create products container
        const productsContainer = document.createElement('div');
        productsContainer.className = 'chatbot-products';
        
        // Add title with products count
        const productsTitle = document.createElement('h4');
        productsTitle.innerHTML = `Sản phẩm gợi ý cho bạn <span class="product-count">(${products.length})</span>:`;
        productsContainer.appendChild(productsTitle);
        
        // Create vertical list for products (changed from horizontal carousel)
        const sliderContainer = document.createElement('div');
        sliderContainer.className = 'product-recommendations-slider-container';
        
        const productsWrapper = document.createElement('div');
        productsWrapper.className = 'product-recommendations-slider';
          // Add products
        products.forEach(product => {
            const productCard = createProductCard(product);
            productsWrapper.appendChild(productCard);
        });
          // Add scroll hint for better UX
        const scrollHint = document.createElement('div');
        scrollHint.className = 'scroll-hint';
        scrollHint.innerHTML = '<i class="fas fa-arrow-down"></i> Kéo để xem thêm';
        
        // Add screen reader text for accessibility
        const srOnly = document.createElement('span');
        srOnly.className = 'sr-only';
        srOnly.textContent = 'Cuộn xuống để xem thêm sản phẩm';
        scrollHint.appendChild(srOnly);
        
        // Properly nest elements
        sliderContainer.appendChild(productsWrapper);
        sliderContainer.appendChild(scrollHint);
        productsContainer.appendChild(sliderContainer);
        
        // Add "Xem tất cả" button if there are multiple products
        if (products.length > 3) {
            const viewAllContainer = document.createElement('div');
            viewAllContainer.className = 'view-all-products';
            
            const viewAllBtn = document.createElement('a');
            viewAllBtn.href = '/products?recommended=true';
            viewAllBtn.className = 'view-all-btn';
            viewAllBtn.innerHTML = '<i class="fas fa-th-list"></i> Xem tất cả sản phẩm';
            viewAllBtn.addEventListener('click', function(e) {
                e.preventDefault();
                // Here you might want to open a modal or navigate to a page with all recommendations
                window.location.href = '/products?recommended=true';
            });
            
            viewAllContainer.appendChild(viewAllBtn);
            productsContainer.appendChild(viewAllContainer);
        }
        
        // Append to message
        messageElement.appendChild(productsContainer);
          // Initialize slider if needed - we keep this for potential future use
        // but it will mostly handle scrolling behavior
        initProductSlider(productsWrapper, sliderContainer);
    }    // Create a product card element - modified for horizontal layout
    function createProductCard(product) {
        const card = document.createElement('div');
        card.className = 'product-card loading'; // Add loading state initially
        card.dataset.productId = product.id;
        
        // Product image - now on the left side
        const imageContainer = document.createElement('div');
        imageContainer.className = 'product-image';
          const image = document.createElement('img');
        image.src = product.imageUrls && product.imageUrls.length > 0 ? product.imageUrls[0] : '/static/img/no-image.png';
        image.alt = product.name;
        image.loading = "lazy"; // Enable lazy loading
        image.onerror = function() {
            this.src = '/static/img/no-image.png';
            card.classList.remove('loading');
        };
        image.onload = function() {
            card.classList.remove('loading');
        };
          // Add discount badge if applicable
        if (product.salePrice && product.salePrice > 0) {
            const discountPercent = Math.round((product.salePrice / product.price) * 100);
            const discountBadge = document.createElement('div');
            discountBadge.className = 'discount-badge';
            discountBadge.textContent = `-${discountPercent}%`;
            imageContainer.appendChild(discountBadge);
        }
        
        // Add "New" badge for recently added products (if available)
        if (product.createdDate) {
            const createdDate = new Date(product.createdDate);
            const currentDate = new Date();
            const daysDifference = Math.floor((currentDate - createdDate) / (1000 * 60 * 60 * 24));
            
            if (daysDifference < 7) { // Products added in the last 7 days
                const newBadge = document.createElement('div');
                newBadge.className = 'new-product-badge';
                newBadge.textContent = 'Mới';
                imageContainer.appendChild(newBadge);
            }
        }
        
        // Add status indicator (if available)
        if (product.status) {
            const statusBadge = document.createElement('div');
            statusBadge.className = 'status-badge';
            
            if (product.status === 'out_of_stock') {
                statusBadge.className += ' out-of-stock';
                statusBadge.textContent = 'Hết hàng';
            } else if (product.status === 'low_stock') {
                statusBadge.className += ' low-stock';
                statusBadge.textContent = 'Sắp hết';
            } else if (product.status === 'pre_order') {
                statusBadge.className += ' pre-order';
                statusBadge.textContent = 'Đặt trước';
            }
            
            if (statusBadge.textContent) {
                imageContainer.appendChild(statusBadge);
            }
        }
        
        imageContainer.appendChild(image);
        card.appendChild(imageContainer);
        
        // Product info - now to the right of the image
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
        const viewButton = document.createElement('a');        viewButton.href = `/product-detail/${product.id}`;
        viewButton.className = 'btn btn-sm btn-primary view-product';
        viewButton.textContent = 'Xem sản phẩm';
        
        info.appendChild(viewButton);
        card.appendChild(info);
        
        // Add product card navigation buttons if product has multiple images
        if (product.imageUrls && product.imageUrls.length > 1) {
            const prevButton = document.createElement('button');
            prevButton.className = 'product-card-nav product-card-prev';
            prevButton.innerHTML = '&#10094;';
            prevButton.setAttribute('aria-label', 'Ảnh trước');
            prevButton.onclick = function(e) {
                e.preventDefault();
                e.stopPropagation();
                cycleProductImage(imageContainer, product.imageUrls, -1);
            };
            
            const nextButton = document.createElement('button');
            nextButton.className = 'product-card-nav product-card-next';
            nextButton.innerHTML = '&#10095;';
            nextButton.setAttribute('aria-label', 'Ảnh sau');
            nextButton.onclick = function(e) {
                e.preventDefault();
                e.stopPropagation();
                cycleProductImage(imageContainer, product.imageUrls, 1);
            };
            
            card.appendChild(prevButton);
            card.appendChild(nextButton);
        }
          // Make the entire card clickable with accessibility improvements
        card.addEventListener('click', function() {
            window.location.href = `/product-detail/${product.id}`;
        });
        
        // Improve accessibility
        card.setAttribute('role', 'button');
        card.setAttribute('aria-label', `Xem chi tiết sản phẩm ${product.name} với giá ${formatCurrency(product.salePrice && product.salePrice > 0 ? product.price - product.salePrice : product.price)}`);
        card.setAttribute('tabindex', '0');
        
        // Support keyboard navigation
        card.addEventListener('keydown', function(e) {
            // Navigate with Enter or Space key
            if (e.key === 'Enter' || e.key === ' ') {
                e.preventDefault();
                window.location.href = `/product-detail/${product.id}`;
            }
        });
        
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
    
    // Function to cycle through product images
    function cycleProductImage(imageContainer, imageUrls, direction) {
        const currentImg = imageContainer.querySelector('img');
        const currentSrc = currentImg.src;
        let currentIndex = 0;
        
        // Find the index of the current image
        for (let i = 0; i < imageUrls.length; i++) {
            if (currentSrc.indexOf(imageUrls[i]) !== -1 || imageUrls[i].indexOf(currentSrc) !== -1) {
                currentIndex = i;
                break;
            }
        }
        
        // Calculate the next image index
        let nextIndex = (currentIndex + direction) % imageUrls.length;
        if (nextIndex < 0) nextIndex = imageUrls.length - 1;
        
        // Create dot indicators if they don't exist
        if (!imageContainer.querySelector('.image-indicators')) {
            const indicators = document.createElement('div');
            indicators.className = 'image-indicators';
            
            for (let i = 0; i < imageUrls.length; i++) {
                const dot = document.createElement('span');
                dot.className = i === currentIndex ? 'indicator active' : 'indicator';
                dot.dataset.index = i;
                dot.addEventListener('click', function(e) {
                    e.stopPropagation();
                    const selectedIndex = parseInt(this.dataset.index);
                    currentImg.src = imageUrls[selectedIndex];
                    updateIndicators(indicators, selectedIndex);
                });
                indicators.appendChild(dot);
            }
            
            imageContainer.appendChild(indicators);
        }
        
        // Animate image change with fade effect
        currentImg.style.opacity = '0';
        setTimeout(() => {
            currentImg.src = imageUrls[nextIndex];
            currentImg.style.opacity = '1';
            
            // Update indicators
            const indicators = imageContainer.querySelector('.image-indicators');
            if (indicators) {
                updateIndicators(indicators, nextIndex);
            }
        }, 200);
    }
    
    // Update image indicators
    function updateIndicators(container, activeIndex) {
        const indicators = container.querySelectorAll('.indicator');
        indicators.forEach((dot, i) => {
            if (i === activeIndex) {
                dot.classList.add('active');
            } else {
                dot.classList.remove('active');
            }
        });
    }// Initialize product slider - modified for vertical scrolling
    function initProductSlider(containerElement, parentContainer) {
        // Add drag scrolling functionality for mobile and desktop
        let isDown = false;
        let startY;
        let scrollTop;
        
        // Hide scroll hint after user interacts with slider
        const hideScrollHint = () => {
            const hint = parentContainer?.querySelector('.scroll-hint');
            if (hint) {
                hint.style.opacity = '0';
                setTimeout(() => {
                    hint.style.display = 'none';
                }, 500);
            }
        };        containerElement.addEventListener('mousedown', (e) => {
            isDown = true;
            containerElement.classList.add('active');
            startY = e.pageY - containerElement.offsetTop;
            scrollTop = containerElement.scrollTop;
            hideScrollHint();
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
            const y = e.pageY - containerElement.offsetTop;
            const walk = (y - startY) * 2.5; // Enhanced scroll speed for smoother experience
            containerElement.scrollTop = scrollTop - walk;
        });
          // Add touch support for mobile devices
        containerElement.addEventListener('touchstart', (e) => {
            startY = e.touches[0].pageY - containerElement.offsetTop;
            scrollTop = containerElement.scrollTop;
            containerElement.classList.add('active');
            hideScrollHint();
        });
        
        containerElement.addEventListener('touchend', () => {
            containerElement.classList.remove('active');
        });
        
        containerElement.addEventListener('touchmove', (e) => {
            if (e.cancelable) e.preventDefault();
            const y = e.touches[0].pageY - containerElement.offsetTop;
            const walk = (y - startY) * 1.5;
            containerElement.scrollTop = scrollTop - walk;
        }, { passive: false });        // If page has jQuery and Slick slider, initialize it with vertical configuration
        if (window.jQuery && typeof jQuery.fn.slick === 'function') {
            try {
                $(containerElement).slick({
                    dots: true,
                    dotsClass: 'slick-dots vertical-dots',
                    infinite: false,
                    speed: 500,
                    slidesToShow: 3,  // Show more items since they're stacked vertically
                    slidesToScroll: 1,
                    vertical: true,   // Changed to vertical orientation
                    verticalSwiping: true,
                    adaptiveHeight: true,
                    arrows: true,
                    accessibility: true,
                    autoplay: false,
                    draggable: true,
                    swipe: true,
                    touchMove: true,
                    waitForAnimate: false, // Faster animation
                    centerMode: false,
                    lazyLoad: 'ondemand', // Lazy loading for better performance
                    prevArrow: '<button type="button" class="slick-prev" aria-label="Sản phẩm trước đó">▲</button>',
                    nextArrow: '<button type="button" class="slick-next" aria-label="Sản phẩm tiếp theo">▼</button>',
                    responsive: [
                        {
                            breakpoint: 1024,
                            settings: {
                                slidesToShow: 3,
                                dots: true
                            }
                        },
                        {
                            breakpoint: 768,
                            settings: {
                                slidesToShow: 2,
                                dots: true,
                                autoplay: true,
                                autoplaySpeed: 4000,
                                pauseOnHover: true,
                                pauseOnFocus: true
                            }
                        },
                        {
                            breakpoint: 480,
                            settings: {
                                slidesToShow: 1,
                                dots: true,
                                autoplay: true,
                                autoplaySpeed: 3000,
                                pauseOnHover: true,
                                pauseOnFocus: true
                            }
                        }
                    ]
                });
                
                // Enhance accessibility for Slick slider
                $(containerElement).find('.slick-dots button').attr('aria-label', 'Chuyển đến trang sản phẩm');
                
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
