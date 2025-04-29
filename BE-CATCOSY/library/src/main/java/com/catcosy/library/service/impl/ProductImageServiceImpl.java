package com.catcosy.library.service.impl;
import com.catcosy.library.repository.ProductImageRepository;
import com.catcosy.library.model.ProductImage;
import com.catcosy.library.service.ProductImageService;
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
        return productImageRepository.findByIdProductUnique(id);
    }
    @Override
    public ProductImage save(ProductImage productImage) {
        return productImageRepository.save(productImage);
    }
}
