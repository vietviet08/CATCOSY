package com.catcosy.customer.service;

import java.util.List;
import java.util.Map;

/**
 * Service interface for looking up database information to enrich chatbot responses
 */
public interface ChatbotDatabaseLookupService {

    /**
     * Get summary information about the store
     * 
     * @return Map of store information including product count, category count, etc.
     */
    Map<String, Object> getStoreSummary();
    
    /**
     * Get all category names
     * 
     * @return List of category names
     */
    List<String> getAllCategoryNames();
    
    /**
     * Get all brand names
     * 
     * @return List of brand names
     */
    List<String> getAllBrandNames();
    
    /**
     * Get product count by category
     * 
     * @return Map where key is category name and value is product count
     */
    Map<String, Long> getProductCountByCategory();
}
