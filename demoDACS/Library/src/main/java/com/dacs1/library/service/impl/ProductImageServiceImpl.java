package com.dacs1.library.service.impl;

import com.dacs1.library.model.ProductImage;
import com.dacs1.library.repository.ProductImageRepository;
import com.dacs1.library.repository.ProductRepository;
import com.dacs1.library.service.ProductImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductImageServiceImpl implements ProductImageService {

    @Autowired
    private ProductImageRepository productImageRepository;

    @Override
    public List<ProductImage> findByIdProductUnique() {
        return productImageRepository.findByIdProductUnique();
    }

    @Override
    public List<ProductImage> findByIdProductUnique(Long id) {
        return productImageRepository.findByIdProduct(id);
    }

    @Override
    public ProductImage save(ProductImage productImage) {
        return productImageRepository.save(productImage);
    }
}
