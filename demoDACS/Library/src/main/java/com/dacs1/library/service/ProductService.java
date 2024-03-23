package com.dacs1.library.service;

import com.dacs1.library.dto.ProductDto;
import com.dacs1.library.model.Product;
import com.dacs1.library.model.Size;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductService {
    List<ProductDto> findAllProduct();

    Product save(List<MultipartFile> images, ProductDto productDto) throws IOException;

    Product update(List<MultipartFile> images, ProductDto productDto) throws IOException;

    void updateProductSize(Long idProduct, Set<Size> sizes);

    Optional<Product> findById(Long id);

    ProductDto getById(Long id);

    void deleteById(Long id);

    void activateById(Long id);

    void removeUnusedSizesFromProducts();

    List<ProductDto> sortDesc();

    List<ProductDto> sortAsc();

    List<ProductDto> byCategory(String nameCategory);

    Page<ProductDto> pageProduct(int pageNo);

    Page<ProductDto> pageProductSearch(String keyword, int pageNo);

}
