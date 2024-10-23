package com.catcosy.library.service;

import com.catcosy.library.model.ProductSize;

import java.util.List;


public interface ProductSizeService {
    List<ProductSize> findAllByIdProduct(Long id);



}
