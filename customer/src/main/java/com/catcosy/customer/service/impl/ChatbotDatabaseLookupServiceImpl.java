package com.catcosy.customer.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.stereotype.Service;

import com.catcosy.customer.service.ChatbotDatabaseLookupService;
import com.catcosy.library.repository.ProductRepository;
import com.catcosy.library.repository.CategoryRepository;
import com.catcosy.library.repository.BrandRepository;
import com.catcosy.library.repository.OrderRepository;
import com.catcosy.library.model.Product;
import com.catcosy.library.model.Category;
import com.catcosy.library.model.Brand;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for looking up common data about the store for use in the chatbot
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotDatabaseLookupServiceImpl implements ChatbotDatabaseLookupService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final OrderRepository orderRepository;
    
    /**
     * Get summary information about the store
     */
    @Override
    public Map<String, Object> getStoreSummary() {
        Map<String, Object> summary = new HashMap<>();
        
        try {
            // Count active products
            long productCount = productRepository.findAllIsActivated().size();
            summary.put("productCount", productCount);
              // Count categories
            List<Category> activeCategories = categoryRepository.findAllByIsActivated(true);
            summary.put("categoryCount", activeCategories.size());
            
            // Count brands
            List<Brand> activeBrands = brandRepository.findAllByBrandIsActivate();
            summary.put("brandCount", activeBrands.size());
            
            // Top categories
            List<String> categoryNames = new ArrayList<>();
            for (Category category : activeCategories) {
                categoryNames.add(category.getName());
                if (categoryNames.size() >= 5) break;
            }
            summary.put("topCategories", categoryNames);
            
            // Top brands
            List<String> brandNames = new ArrayList<>();
            for (Brand brand : activeBrands) {
                brandNames.add(brand.getName());
                if (brandNames.size() >= 5) break;
            }
            summary.put("topBrands", brandNames);
            
        } catch (Exception e) {
            log.error("Error getting store summary", e);
        }
        
        return summary;
    }
    
    /**
     * Get all category names
     */    @Override
    public List<String> getAllCategoryNames() {
        List<String> categories = new ArrayList<>();
        try {
            List<Category> activeCategories = categoryRepository.findAllByIsActivated(true);
            for (Category category : activeCategories) {
                categories.add(category.getName());
            }
        } catch (Exception e) {
            log.error("Error getting category names", e);
        }
        return categories;
    }
    
    /**
     * Get all brand names
     */
    @Override
    public List<String> getAllBrandNames() {
        List<String> brands = new ArrayList<>();
        try {
            List<Brand> activeBrands = brandRepository.findAllByBrandIsActivate();
            for (Brand brand : activeBrands) {
                brands.add(brand.getName());
            }
        } catch (Exception e) {
            log.error("Error getting brand names", e);
        }
        return brands;
    }
    
    /**
     * Get product count by category
     */    @Override
    public Map<String, Long> getProductCountByCategory() {
        Map<String, Long> counts = new HashMap<>();
        try {
            List<Category> categories = categoryRepository.findAllByIsActivated(true);
            for (Category category : categories) {
                List<Product> products = productRepository.findAllByCategory(category.getName());
                counts.put(category.getName(), (long) products.size());
            }
        } catch (Exception e) {
            log.error("Error getting product counts by category", e);
        }
        return counts;
    }
}
