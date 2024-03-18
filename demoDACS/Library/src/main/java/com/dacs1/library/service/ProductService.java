package com.dacs1.library.service;

import com.dacs1.library.dto.ProductDto;
import com.dacs1.library.model.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    List<ProductDto> findAllProduct();

    Product save(MultipartFile img1, MultipartFile img2, MultipartFile img3, MultipartFile img4, ProductDto productDto) throws IOException;

    Product update(MultipartFile img1, MultipartFile img2, MultipartFile img3, MultipartFile img4, ProductDto productDto) throws IOException;

    ProductDto findById(Long id);

    void deleteById(Long id);

    void activateById(Long id);
}
