package com.catcosy.customer.service;

import java.util.List;
import com.catcosy.library.dto.ProductDto;

/**
 * Service interface for retrieving product information for the chatbot
 */
public interface ChatbotProductService {

    /**
     * Get a list of recommended products based on search criteria
     * 
     * @param query The search query
     * @param limit Maximum number of products to return
     * @return List of product DTOs
     */
    List<ProductDto> getRecommendedProducts(String query, int limit);
    
    /**
     * Get top trending products
     * 
     * @param limit Maximum number of products to return
     * @return List of product DTOs
     */
    List<ProductDto> getTrendingProducts(int limit);
    
    /**
     * Get products on sale (with discount)
     * 
     * @param limit Maximum number of products to return
     * @return List of product DTOs
     */
    List<ProductDto> getProductsOnSale(int limit);
    
    /**
     * Get products from a specific category
     * 
     * @param categoryName The category name
     * @param limit Maximum number of products to return
     * @return List of product DTOs
     */
    List<ProductDto> getProductsByCategory(String categoryName, int limit);
    
    /**
     * Get products from a specific brand
     * 
     * @param brandName The brand name
     * @param limit Maximum number of products to return
     * @return List of product DTOs
     */
    List<ProductDto> getProductsByBrand(String brandName, int limit);
    
    /**
     * Get product by ID
     * 
     * @param productId The product ID
     * @return ProductDto or null if not found
     */
    ProductDto getProductById(Long productId);
}
