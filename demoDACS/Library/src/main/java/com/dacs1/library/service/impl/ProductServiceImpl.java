package com.dacs1.library.service.impl;

import com.dacs1.library.dto.ProductDto;
import com.dacs1.library.model.Product;
import com.dacs1.library.model.ProductImage;
import com.dacs1.library.repository.ProductImageRepository;
import com.dacs1.library.repository.ProductRepository;
import com.dacs1.library.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    private List<ProductDto> convertToDtoList(List<Product> products) {
        List<ProductDto> dtoList = new ArrayList<>();
        for (Product product : products) {
            ProductDto productDto = new ProductDto();
            productDto.setId(product.getId());
            productDto.setName(product.getName());
            productDto.setSizes(product.getSizes().stream().toList());
            productDto.setDescription(product.getDescription());
            productDto.setCostPrice(product.getCostPrice());
            productDto.setSalePrice(product.getSalePrice());
            productDto.setQuantity(product.getQuantity());
            productDto.setImages(product.getImages());
            productDto.setCategory(product.getCategory());
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
        product.setSizes(productDto.getSizes().stream().toList());
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
        productDto.setSizes(product.getSizes().stream().toList());
        productDto.setDescription(product.getDescription());
        productDto.setCostPrice(product.getCostPrice());
        productDto.setSalePrice(product.getSalePrice());
        productDto.setQuantity(product.getQuantity());
        productDto.setImages(product.getImages());
        productDto.setCategory(product.getCategory());
        productDto.setActivated(product.getIsActivated());
        productDto.setDeleted(product.getIsDeleted());
        return productDto;
    }

    @Override
    public List<ProductDto> findAllProduct() {
        return convertToDtoList(productRepository.findAll());
    }

    @Override
    public Product save(List<MultipartFile> images, ProductDto productDto) throws IOException {
        Product product = new Product();
        try {
            product.setId(productDto.getId());
            product.setName(productDto.getName());
            product.setDescription(productDto.getDescription());
            product.setSizes(productDto.getSizes().stream().toList());
            product.setCostPrice(productDto.getCostPrice());
            product.setSalePrice(productDto.getSalePrice());
            product.setQuantity(productDto.getQuantity());
            product.setCategory(productDto.getCategory());
            product.setIsActivated(true);
            product.setIsDeleted(false);

            List<ProductImage> imageList = new ArrayList<>();

            for (MultipartFile image : images) {
                ProductImage productImage = new ProductImage();
                String fileImg = Base64.getEncoder().encodeToString(image.getBytes());
                productImage.setImage(fileImg);
                imageList.add(productImage);
            }
            product.setImages(imageList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return productRepository.save(product);
    }

    @Override
    public Product update(List<MultipartFile> images, ProductDto productDto) throws IOException {

        Product product = productRepository.getReferenceById(productDto.getId());

        List<ProductImage> productImagesOld = productImageRepository.findByIdProduct(productDto.getId());

        int i = 0;
        for (MultipartFile newImage : images) {
            if (newImage != null) {
                ProductImage productImage = productImagesOld.get(i);
                productImage.setImage(Base64.getEncoder().encodeToString(newImage.getBytes()));
                productImagesOld.add(i, productImage);
            }
            i++;
        }

        product.setId(productDto.getId());
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setSizes(productDto.getSizes().stream().toList());
        product.setCostPrice(productDto.getCostPrice());
        product.setSalePrice(productDto.getSalePrice());
        product.setQuantity(productDto.getQuantity());
        product.setCategory(productDto.getCategory());
        product.setIsActivated(productDto.getActivated());
        product.setIsDeleted(productDto.getDeleted());

        return productRepository.save(product);
    }

    @Override
    public ProductDto findById(Long id) {
        return convertToDto(productRepository.getReferenceById(id));
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
}
