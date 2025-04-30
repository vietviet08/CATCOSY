package com.catcosy.library.service;

import com.catcosy.library.dto.ProductDto;
import com.catcosy.library.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> getAllProduct();

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
                                                  List<Long> size,
                                                  List<Long> brand);

    List<ProductDto> productRandomLimit(int limit);

    List<ProductDto> productSaleRandomLimit(int limit);

    List<ProductDto> productRandomSameCategoryLimit(Long idCategory, Long idProduct, int limit);

    Product updateWithMixedImages(ProductDto productDto, List<MultipartFile> newImages, List<String> oldImagesBase64, List<Integer> deletedImageIds, List<Long> sizes) throws IOException;
}
