package com.catcosy.customer.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data structure for enriching chatbot messages with product data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatbotContext {
    
    // Source type of the context (products, categories, etc.)
    private String type;
    
    // List of products
    private List<Product> products;
    
    // For any other context data
    private Map<String, Object> metadata;
    
    /**
     * Add a product to the context
     */
    public void addProduct(Product product) {
        if (products == null) {
            products = new ArrayList<>();
        }
        products.add(product);
    }
    
    /**
     * Add metadata
     */
    public void addMetadata(String key, Object value) {
        if (metadata == null) {
            metadata = new HashMap<>();
        }
        metadata.put(key, value);
    }
    
    /**
     * Product data model
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Product {
        private Long id;
        private String name;
        private Double price;
        private Double salePrice;
        private String description;
        private String category;
        private String brand;
        private List<String> imageUrls;
        private Map<String, Object> attributes;
        
        /**
         * Add attribute
         */
        public void addAttribute(String key, Object value) {
            if (attributes == null) {
                attributes = new HashMap<>();
            }
            attributes.put(key, value);
        }
    }
}
