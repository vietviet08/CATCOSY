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
    public Product update(List<MultipartFile> newImages, List<Long> newSizesId, ProductDto productDto) throws IOException {
        Product product = productRepository.getReferenceById(productDto.getId());

        // Update basic product information
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setCostPrice(productDto.getCostPrice());
        product.setSalePrice(productDto.getSalePrice());
        product.setCategory(productDto.getCategory());
        product.setBrand(productDto.getBrand());

        System.out.println("Updating product with ID: " + productDto.getId());
        
        // Lưu ảnh hiện có của sản phẩm
        List<ProductImage> existingImages = new ArrayList<>(product.getImages());
        
        // Xử lý trường hợp có ảnh mới được tải lên
        boolean hasNewImages = newImages != null && !newImages.isEmpty();
        boolean hasValidNewImages = false;
        
        // Kiểm tra xem có ảnh mới hợp lệ không
        if (hasNewImages) {
            for (MultipartFile image : newImages) {
                if (image != null && !image.isEmpty()) {
                    hasValidNewImages = true;
                    break;
                }
            }
        }
        
        // Nếu có ảnh mới hợp lệ, thì xóa ảnh cũ và thêm ảnh mới
        if (hasValidNewImages) {
            System.out.println("Found new images, updating product images");
            
            // Xóa tất cả ảnh cũ của sản phẩm
            productImageRepository.deleteAllByProductId(product.getId());
            
            // Thêm ảnh mới
            List<ProductImage> updatedImages = new ArrayList<>();
            for (MultipartFile image : newImages) {
                if (image != null && !image.isEmpty()) {
                    ProductImage productImage = new ProductImage();
                    String fileImg = Base64.getEncoder().encodeToString(image.getBytes());
                    productImage.setProduct(product);
                    productImage.setImage(fileImg);
                    productImageRepository.save(productImage);
                    updatedImages.add(productImage);
                    System.out.println("Added new image to product");
                }
            }
            
            // Cập nhật danh sách ảnh cho sản phẩm
            product.setImages(updatedImages);
            System.out.println("Updated product with " + updatedImages.size() + " new images");
        } else {
            // Nếu không có ảnh mới, giữ nguyên ảnh cũ
            System.out.println("No new valid images, keeping existing images: " + existingImages.size());
        }

        // Update product sizes
        updateProductSize(product, newSizesId);

        // Save and return updated product
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product updateWithMixedImages(ProductDto productDto, List<MultipartFile> newImages, List<String> oldImagesBase64, 
                                        List<Integer> deletedImageIds, List<Long> newSizesId) throws IOException {
        Product product = productRepository.getReferenceById(productDto.getId());

        // Cập nhật thông tin cơ bản của sản phẩm
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setCostPrice(productDto.getCostPrice());
        product.setSalePrice(productDto.getSalePrice());
        product.setCategory(productDto.getCategory());
        product.setBrand(productDto.getBrand());

        System.out.println("Updating product with ID: " + productDto.getId() + " using mixed images strategy");
        
        try {
            // Xóa tất cả ảnh cũ để thêm lại ảnh đã chọn 
            productImageRepository.deleteAllByProductId(product.getId());
            System.out.println("Deleted all old images for product ID: " + product.getId());
            
            // Danh sách ảnh mới sẽ được lưu
            List<ProductImage> updatedImages = new ArrayList<>();
            
            // 1. Xử lý ảnh cũ được giữ lại (nếu có)
            if (oldImagesBase64 != null && !oldImagesBase64.isEmpty()) {
                System.out.println("Processing " + oldImagesBase64.size() + " retained old images");
                
                // Duyệt qua và lưu từng ảnh cũ vào database
                for (int i = 0; i < oldImagesBase64.size(); i++) {
                    String base64Image = oldImagesBase64.get(i);
                    if (base64Image != null && !base64Image.isEmpty()) {
                        ProductImage productImage = new ProductImage();
                        productImage.setProduct(product);
                        productImage.setImage(base64Image);
                        ProductImage savedImage = productImageRepository.save(productImage);
                        updatedImages.add(savedImage);
                        System.out.println("Retained old image " + i + ", new DB ID: " + savedImage.getId());
                    }
                }
            }
            
            // 2. Xử lý ảnh mới được tải lên (nếu có)
            if (newImages != null && !newImages.isEmpty()) {
                System.out.println("Processing " + newImages.size() + " new images");
                
                // Duyệt qua và lưu từng ảnh mới vào database
                for (int i = 0; i < newImages.size(); i++) {
                    MultipartFile image = newImages.get(i);
                    if (image != null && !image.isEmpty()) {
                        ProductImage productImage = new ProductImage();
                        String fileImg = Base64.getEncoder().encodeToString(image.getBytes());
                        productImage.setProduct(product);
                        productImage.setImage(fileImg);
                        ProductImage savedImage = productImageRepository.save(productImage);
                        updatedImages.add(savedImage);
                        System.out.println("Added new uploaded image " + i + ", new DB ID: " + savedImage.getId());
                    }
                }
            }
            
            // Log chi tiết về ảnh sau khi cập nhật
            if (updatedImages.isEmpty()) {
                System.out.println("Warning: No images for product after update");
            } else {
                System.out.println("Total images after update: " + updatedImages.size());
                for (int i = 0; i < updatedImages.size(); i++) {
                    ProductImage img = updatedImages.get(i);
                    System.out.println("Image " + i + " - ID: " + img.getId());
                }
            }
            
            // Cập nhật danh sách ảnh cho sản phẩm
            product.setImages(updatedImages);
            
            // Cập nhật kích thước sản phẩm
            updateProductSize(product, newSizesId);
            
            // Lưu và trả về sản phẩm đã cập nhật
            return productRepository.save(product);
        } catch (Exception e) {
            System.err.println("Error in updateWithMixedImages: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
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
