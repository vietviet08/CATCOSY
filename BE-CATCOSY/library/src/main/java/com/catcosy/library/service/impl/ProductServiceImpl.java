package com.catcosy.library.service.impl;

import com.catcosy.library.repository.ProductImageRepository;
import com.catcosy.library.repository.ProductRepository;
import com.catcosy.library.repository.ProductSizeRepository;
import com.catcosy.library.repository.SizeRepository;
import com.catcosy.library.dto.ProductDto;
import com.catcosy.library.model.Product;
import com.catcosy.library.model.ProductImage;
import com.catcosy.library.model.ProductSize;
import com.catcosy.library.model.Size;
import com.catcosy.library.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private ProductSizeRepository productSizeRepository;

    @Autowired
    private SizeRepository sizeRepository;

    private List<ProductDto> convertToDtoList(List<Product> products) {
        List<ProductDto> dtoList = new ArrayList<>();
        for (Product product : products) {
            ProductDto productDto = new ProductDto();
            productDto.setId(product.getId());
            productDto.setName(product.getName());
            productDto.setSizes(product.getSizes());
            productDto.setDescription(product.getDescription());
            productDto.setCostPrice(product.getCostPrice());
            productDto.setSalePrice(product.getSalePrice());
            productDto.setQuantity(product.getQuantity());
            List<String> strings = new ArrayList<>();
            for (ProductImage productImage : product.getImages()) {
                strings.add(productImage.getImage());
            }
            productDto.setImages(strings);
            productDto.setCategory(product.getCategory());
            productDto.setBrand(product.getBrand());
            productDto.setActivated(product.getIsActivated());
            productDto.setDeleted(product.getIsDeleted());
            dtoList.add(productDto);
        }
        return dtoList;
    }

    private Product convertToEntity(ProductDto productDto) {
        Product product = new Product();
        product.setId(productDto.getId());
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setSizes(productDto.getSizes());
        product.setCostPrice(productDto.getCostPrice());
        product.setSalePrice(productDto.getSalePrice());
        product.setQuantity(productDto.getQuantity());
        product.setCategory(productDto.getCategory());
        product.setIsActivated(productDto.getActivated());
        product.setIsDeleted(productDto.getDeleted());
        return product;
    }

    private ProductDto convertToDto(Product product) {
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setSizes(product.getSizes());
        productDto.setDescription(product.getDescription());
        productDto.setCostPrice(product.getCostPrice());
        productDto.setSalePrice(product.getSalePrice());
        productDto.setQuantity(product.getQuantity());
        List<String> strings = new ArrayList<>();
        for (ProductImage productImage : product.getImages()) {
            strings.add(productImage.getImage());
        }
        productDto.setImages(strings);
        productDto.setCategory(product.getCategory());
        productDto.setBrand(product.getBrand());
        productDto.setActivated(product.getIsActivated());
        productDto.setDeleted(product.getIsDeleted());
        return productDto;
    }

    @Override
    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }

    @Override
    public List<ProductDto> findAllProduct() {
        return convertToDtoList(productRepository.findAll());
    }

    @Override
    public Product save(List<MultipartFile> images, List<Long> sizes, ProductDto productDto) throws IOException {
        Product product = new Product();

        try {
            product.setId(productDto.getId());
            product.setName(productDto.getName());
            product.setDescription(productDto.getDescription());
            product.setCostPrice(productDto.getCostPrice());
            product.setSalePrice(productDto.getSalePrice());
            product.setQuantity(0);
            product.setCategory(productDto.getCategory());
            product.setBrand(productDto.getBrand());
            product.setIsActivated(true);
            product.setIsDeleted(false);

            // Save the product first to get an ID
            product = productRepository.save(product);
            
            // Process images if they exist
            if (images != null) {
                List<ProductImage> imageList = new ArrayList<>();
                for (MultipartFile image : images) {
                    if (image != null && !image.isEmpty()) {
                        ProductImage productImage = new ProductImage();
                        String fileImg = Base64.getEncoder().encodeToString(image.getBytes());
                        productImage.setProduct(product);
                        productImage.setImage(fileImg);
                        imageList.add(productImage);
                        productImageRepository.save(productImage);
                    }
                }
                
                if (!imageList.isEmpty()) {
                    product.setImages(imageList);
                } else {
                    product.setImages(new ArrayList<>());
                }
            } else {
                product.setImages(new ArrayList<>());
            }

            // Process sizes if they exist
            List<ProductSize> productSizes = new ArrayList<>();
            if (sizes != null && !sizes.isEmpty()) {
                for (Long id : sizes) {
                    ProductSize productSize = new ProductSize();
                    Size size = sizeRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Size not found with id: " + id));
                    productSize.setSize(size);
                    productSize.setQuantity(0);
                    productSize.setProduct(product);
                    productSizes.add(productSize);
                    productSizeRepository.save(productSize);
                }
                product.setSizes(productSizes);
            } else {
                product.setSizes(new ArrayList<>());
            }
            
            // Save the product again with all relationships
            return productRepository.save(product);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional
    public Product update(List<MultipartFile> images, List<Long> newSizesId, ProductDto productDto) throws IOException {
        Product product = productRepository.getReferenceById(productDto.getId());

        // Update basic product information
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setCostPrice(productDto.getCostPrice());
        product.setSalePrice(productDto.getSalePrice());
        product.setCategory(productDto.getCategory());
        product.setBrand(productDto.getBrand());

        // Process images if they exist
        if (images != null) {
            // Check if there are any valid images
            boolean hasValidImages = false;
            for (MultipartFile image : images) {
                if (image != null && !image.isEmpty()) {
                    hasValidImages = true;
                    break;
                }
            }
            
            // Only update images if we have valid ones
            if (hasValidImages) {
                // Delete old images
                productImageRepository.deleteAllByProductId(product.getId());
                
                // Add new images
                List<ProductImage> newImages = new ArrayList<>();
                for (MultipartFile image : images) {
                    if (image != null && !image.isEmpty()) {
                        ProductImage productImage = new ProductImage();
                        String fileImg = Base64.getEncoder().encodeToString(image.getBytes());
                        productImage.setProduct(product);
                        productImage.setImage(fileImg);
                        productImageRepository.save(productImage);
                        newImages.add(productImage);
                    }
                }
                product.setImages(newImages);
            }
        }

        // Update product sizes
        updateProductSize(product, newSizesId);

        // Save and return updated product
        return productRepository.save(product);
    }

    @Override
    public void updateProductSize(Product product, List<Long> newSizesId) {
        List<ProductSize> productSizes = product.getSizes();
        
        // Clear all sizes if the new size list is empty
        if (newSizesId.isEmpty()) {
            // Adjust the total quantity
            for (ProductSize productSize : productSizes) {
                product.setQuantity(product.getQuantity() - productSize.getQuantity());
            }
            productSizes.clear();
            return;
        }

        // Remove sizes that are not in the new list
        Iterator<ProductSize> iterator = productSizes.iterator();
        while (iterator.hasNext()) {
            ProductSize productSize = iterator.next();
            if (!newSizesId.contains(productSize.getSize().getId())) {
                product.setQuantity(product.getQuantity() - productSize.getQuantity());
                iterator.remove();
            }
        }

        // Add new sizes
        for (Long newSizeId : newSizesId) {
            if (!productSizes.stream().anyMatch(ps -> ps.getSize().getId().equals(newSizeId))) {
                ProductSize productSize = new ProductSize();
                Size size = sizeRepository.findById(newSizeId).orElseThrow(() -> new RuntimeException("Size not found with id: " + newSizeId));
                productSize.setSize(size);
                productSize.setQuantity(0);
                productSize.setProduct(product);
                productSizes.add(productSize);
            }
        }
    }


    public void removeSize(Long id, Product product) {
        List<ProductSize> oldSizes = product.getSizes();
        for (ProductSize size : oldSizes) {
            if (size.getSize().getId().equals(id)) {
                System.out.println("ok");
                oldSizes.remove(size);
                return;
            }
        }
    }


    @Override
    public Optional<Product> findById(Long id) {

        return productRepository.findById(id);
    }

    @Override
    public ProductDto getById(Long id) {
        return convertToDto(productRepository.getByIdProduct(id));
    }

    @Override
    public void deleteById(Long id) {
        Product product = productRepository.getReferenceById(id);
        product.setIsDeleted(true);
        product.setIsActivated(false);
        productRepository.save(product);
    }

    @Override
    public void activateById(Long id) {
        Product product = productRepository.getReferenceById(id);
        product.setIsDeleted(false);
        product.setIsActivated(true);
        productRepository.save(product);
    }

    @Override
    public void removeUnusedSizesFromProducts() {

    }

    @Override
    public Page<ProductDto> sortDesc(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return toPage(pageable, convertToDtoList(productRepository.findAllByPriceDesc()));
    }

    @Override
    public Page<ProductDto> sortAsc(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        return toPage(pageable, convertToDtoList(productRepository.findAllByPriceAsc()));
    }

    @Override
    public List<ProductDto> byCategory(String nameCategory) {
        return convertToDtoList(productRepository.findAllByCategory(nameCategory));
    }

    @Override
    public Page<ProductDto> pageProduct(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        List<ProductDto> dtoList = convertToDtoList(productRepository.findAll());
        return toPage(pageable, dtoList);
    }

    @Override
    public Page<ProductDto> pageProductSearch(String keyword, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        List<ProductDto> dtoList = convertToDtoList(productRepository.findByKeyword(keyword));
        return toPage(pageable, dtoList);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.getByIdProduct(id);
    }

    @Override
    public List<MultipartFile> getImagesById(Long id) {
        return null;
    }

    @Override
    public Page<ProductDto> pageProductIsActivated(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        List<ProductDto> dtoList = convertToDtoList(productRepository.findAllIsActivated());
        return toPage(pageable, dtoList);
    }

    @Override
    public Page<ProductDto> pageProductIsActivatedFilter(int pageNo, int pageSize,
                                                         String keyword,
                                                         String sortPrice,
                                                         Long idCategory,
                                                         Integer minPrice,
                                                         Integer maxPrice,
                                                         List<Long> size,
                                                         List<Long> brand) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
//        if (!sortPrice && (keyword.isEmpty() || keyword == null || keyword.equals(""))) {
//            List<ProductDto> dtoList = convertToDtoList(productRepository.findAllIsActivatedFilter());
//            return toPage(pageable, dtoList);
//        } else if (!sortPrice) {
//            if(keyword != null) keyword = keyword.trim();
//            else keyword = "";
//            List<ProductDto> dtoList = convertToDtoList(productRepository.findAllIsActivatedFilterSearch(keyword));
//            return toPage(pageable, dtoList);
//        } else {
//            if(keyword != null) keyword = keyword.trim();
//            else keyword = "";
//            List<ProductDto> dtoList = convertToDtoList(productRepository.findAllIsActivatedFilterSortPrice(keyword));
//            return toPage(pageable, dtoList);
//        }

        List<ProductDto> dtoList = convertToDtoList(productRepository.findAllIsActivatedFilter(keyword, sortPrice, idCategory, minPrice, maxPrice, size, brand));
        return toPage(pageable, dtoList);
    }

    @Override
    public List<ProductDto> productRandomLimit(int limit) {
        return convertToDtoList(productRepository.findAllRandomProduct(limit));
    }

    @Override
    public List<ProductDto> productSaleRandomLimit(int limit) {
        return convertToDtoList(productRepository.findAllRandomProductSale(limit));
    }

    @Override
    public List<ProductDto> productRandomSameCategoryLimit(Long idCategory, Long idProduct, int limit) {
        return convertToDtoList(productRepository.findAllProductSameCategory(idCategory, idProduct, limit));
    }


//    public List<ProductDto> productRandomSameCategoryLimit(Long idCategory) {
//        return convertToDtoList(productRepository.findAllProductSameCategory(idCategory));
//    }

    private Page<ProductDto> toPage(Pageable pageable, List<ProductDto> list) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), list.size());
        return new PageImpl<ProductDto>(list.subList(start, end), pageable, list.size());
    }
}
