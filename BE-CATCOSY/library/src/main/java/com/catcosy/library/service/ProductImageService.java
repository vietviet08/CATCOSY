package com.catcosy.library.service;

import com.catcosy.library.model.ProductImage;

import java.util.List;


public interface ProductImageService {

    List<ProductImage> findByIdProductUnique();

    List<ProductImage> findByIdProductUnique(Long id);


    ProductImage save (ProductImage productImage);



}
