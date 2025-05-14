package com.catcosy.customer.service.impl;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.catcosy.customer.service.ChatbotProductService;
import com.catcosy.library.dto.ProductDto;
import com.catcosy.library.service.ProductService;
import com.catcosy.library.service.CategoryService;
import com.catcosy.library.service.BrandService;
import com.catcosy.library.model.Category;
import com.catcosy.library.model.Brand;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotProductServiceImpl implements ChatbotProductService {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final BrandService brandService;
    
    @Override
    public List<ProductDto> getRecommendedProducts(String query, int limit) {
        try {
            // First try to find products by keyword
            List<ProductDto> products = productService.pageProductSearch(query, 0, limit * 2)
                .getContent()
                .stream()
                .filter(p -> p.getActivated() && !p.getDeleted())
                .limit(limit)
                .collect(Collectors.toList());
                
            // If we found products, return them
            if (!products.isEmpty()) {
                return products;
            }
            
            // Otherwise, return random products
            return productService.productRandomLimit(limit);
        } catch (Exception e) {
            log.error("Error fetching recommended products", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<ProductDto> getTrendingProducts(int limit) {
        try {
            // For trending products, we'll use random products for now
            // In a real implementation, this could be based on order count or view count
            return productService.productRandomLimit(limit);
        } catch (Exception e) {
            log.error("Error fetching trending products", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<ProductDto> getProductsOnSale(int limit) {
        try {
            return productService.productSaleRandomLimit(limit);
        } catch (Exception e) {
            log.error("Error fetching sale products", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<ProductDto> getProductsByCategory(String categoryName, int limit) {
        try {
            return productService.byCategory(categoryName)
                .stream()
                .filter(p -> p.getActivated() && !p.getDeleted())
                .limit(limit)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching products by category", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<ProductDto> getProductsByBrand(String brandName, int limit) {
        try {
            // Find brand by name
            List<Brand> brands = brandService.findAllBrandIsActivate()
                .stream()
                .filter(b -> b.getName().equalsIgnoreCase(brandName))
                .collect(Collectors.toList());
                
            if (brands.isEmpty()) {
                return new ArrayList<>();
            }
            
            // For now, we'll use filtering of all products to find by brand
            // In a real implementation with many products, this should use a repository method
            List<Long> brandIds = brands.stream()
                .map(Brand::getId)
                .collect(Collectors.toList());
                
            // Use filter method
            return productService.pageProductIsActivatedFilter(
                0, 
                limit,
                "", // No keyword filter
                "news", // Sort by newest
                0L, // No category filter
                null, // No min price
                null, // No max price
                new ArrayList<>(), // No size filter
                brandIds // Filter by brand IDs
            ).getContent();
        } catch (Exception e) {
            log.error("Error fetching products by brand", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public ProductDto getProductById(Long productId) {
        try {
            return productService.getById(productId);
        } catch (Exception e) {
            log.error("Error fetching product by ID", e);
            return null;
        }
    }
}
