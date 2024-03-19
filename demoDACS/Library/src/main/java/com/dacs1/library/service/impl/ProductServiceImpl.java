package com.dacs1.library.service.impl;

import com.dacs1.library.dto.ProductDto;
import com.dacs1.library.model.Product;
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
            productDto.setImg1(product.getImg1());
            productDto.setImg2(product.getImg2());
            productDto.setImg3(product.getImg3());
            productDto.setImg4(product.getImg4());
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
        productDto.setImg1(product.getImg1());
        productDto.setImg2(product.getImg2());
        productDto.setImg3(product.getImg3());
        productDto.setImg4(product.getImg4());
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
    public Product save(MultipartFile img1, MultipartFile img2, MultipartFile img3, MultipartFile img4, ProductDto productDto) throws IOException {
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

            if (img1 == null) {
                product.setImg1(null);
            } else {
                product.setImg1(Base64.getEncoder().encodeToString(img1.getBytes()));
            }
            if (img2 == null) {
                product.setImg2(null);
            } else {
                product.setImg2(Base64.getEncoder().encodeToString(img2.getBytes()));
            }
            if (img3 == null) {
                product.setImg3(null);
            } else {
                product.setImg3(Base64.getEncoder().encodeToString(img3.getBytes()));
            }
            if (img4 == null) {
                product.setImg4(null);
            } else {
                product.setImg4(Base64.getEncoder().encodeToString(img4.getBytes()));
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return productRepository.save(product);
    }

    @Override
    public Product update(MultipartFile img1, MultipartFile img2, MultipartFile img3, MultipartFile img4, ProductDto productDto) throws IOException {

        Product product = productRepository.getReferenceById(productDto.getId());
        if(img1 != null){
            product.setImg1(Base64.getEncoder().encodeToString(img1.getBytes()));
        }
        if(img2 != null){
            product.setImg1(Base64.getEncoder().encodeToString(img2.getBytes()));
        }
        if(img3 != null){
            product.setImg1(Base64.getEncoder().encodeToString(img3.getBytes()));
        }
        if(img4 != null){
            product.setImg1(Base64.getEncoder().encodeToString(img4.getBytes()));
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
