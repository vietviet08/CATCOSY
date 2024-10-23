package com.catcosy.library.service.impl;

import com.catcosy.library.repository.ProductSizeRepository;
import com.catcosy.library.model.ProductSize;
import com.catcosy.library.service.ProductSizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductSizeServiceImpl implements ProductSizeService {

    @Autowired
    private ProductSizeRepository productSizeRepository;

    @Override
    public List<ProductSize> findAllByIdProduct(Long id) {
        return productSizeRepository.findAllByIdProduct(id);
    }


}
