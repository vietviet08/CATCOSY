package com.dacs1.library.service;

import com.dacs1.library.dto.ProductDto;
import com.dacs1.library.model.Product;

import java.util.List;

public interface ProductService {
    List<ProductDto> findAllProduct();



    ProductDto findById(Long id);
}
