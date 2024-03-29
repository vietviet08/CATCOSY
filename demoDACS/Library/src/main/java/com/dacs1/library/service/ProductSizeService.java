package com.dacs1.library.service;

import com.dacs1.library.model.ProductSize;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


public interface ProductSizeService {

    List<ProductSize> findAllByIdProduct(Long id);
}
