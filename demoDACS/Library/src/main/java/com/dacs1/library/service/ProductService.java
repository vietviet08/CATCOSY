package com.dacs1.library.service;

import com.dacs1.library.dto.ProductDto;
import com.dacs1.library.model.Product;
import com.dacs1.library.model.ProductSize;
import com.dacs1.library.model.Size;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductService {
    List<ProductDto> findAllProduct();

    Product save(List<MultipartFile> images, List<Long> sizes, ProductDto productDto) throws IOException;

    Product update(List<MultipartFile> images, List<Long> sizes, ProductDto productDto) throws IOException;

    void updateProductSize(Product product, List<Long> newSizes);

    Optional<Product> findById(Long id);

    ProductDto getById(Long id);

    void deleteById(Long id);

    void activateById(Long id);

    void removeUnusedSizesFromProducts();

    Page<ProductDto> sortDesc(int pageNo, int pageSize);

    Page<ProductDto> sortAsc(int pageNo, int pageSize);

    List<ProductDto> byCategory(String nameCategory);

    Page<ProductDto> pageProduct(int pageNo, int pageSize);

    Page<ProductDto> pageProductSearch(String keyword, int pageNo, int pageSize);

//    Customer

    Product getProductById(Long id);

    List<MultipartFile> getImagesById(Long id);

    Page<ProductDto> pageProductIsActivated(int pageNo, int pageSize);

    Page<ProductDto> pageProductIsActivatedFilter(int pageNo, int pageSize,
                                                  String keyword,
                                                  String sortPrice,
                                                  Long idCategory,
                                                  Integer minPrice,
                                                  Integer maxPrice,
                                                  List<Long> size);

    List<ProductDto> productRandomLimit(int limit);

    List<ProductDto> productRandomSameCategoryLimit(Long idCategory, Long idProduct);



}
