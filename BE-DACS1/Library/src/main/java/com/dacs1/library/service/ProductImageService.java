package com.dacs1.library.service;

import com.dacs1.library.model.Product;
import com.dacs1.library.model.ProductImage;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;


public interface ProductImageService {

    List<ProductImage> findByIdProductUnique();

    List<ProductImage> findByIdProductUnique(Long id);


    ProductImage save (ProductImage productImage);



}
