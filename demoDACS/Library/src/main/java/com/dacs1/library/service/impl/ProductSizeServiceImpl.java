package com.dacs1.library.service.impl;

import com.dacs1.library.model.ProductSize;
import com.dacs1.library.repository.ProductSizeRepository;
import com.dacs1.library.service.ProductSizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
