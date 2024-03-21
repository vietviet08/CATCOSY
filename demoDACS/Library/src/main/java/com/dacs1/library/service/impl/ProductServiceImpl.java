package com.dacs1.library.service.impl;

import com.dacs1.library.dto.ProductDto;
import com.dacs1.library.model.Product;
import com.dacs1.library.model.ProductImage;
import com.dacs1.library.model.Size;
import com.dacs1.library.repository.ProductImageRepository;
import com.dacs1.library.repository.ProductRepository;
import com.dacs1.library.repository.SizeRepository;
import com.dacs1.library.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private SizeRepository sizeRepository;

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
                productImage.setProduct(product);
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

        List<ProductImage> productImagesOlds = productImageRepository.findByIdProduct(productDto.getId());
        List<ProductImage> productImages = new ArrayList<>();

        int i = 0;
        for (MultipartFile newImage : images) {
            if (newImage != null && !newImage.isEmpty()) {
                ProductImage productImage = null;
                if (i < productImagesOlds.size()) {
                    productImage = productImagesOlds.get(i);
                    productImage.setImage(Base64.getEncoder().encodeToString(newImage.getBytes()));

                } else {
                    productImage = new ProductImage();
                    productImage.setImage(Base64.getEncoder().encodeToString(newImage.getBytes()));
                    productImage.setProduct(product);
                }

                productImages.add(productImage);
            }
            i++;
        }

        product.setImages(productImages);

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
    @Transactional
    public void updateProductSize(Long idProduct, List<Size> sizes) {

        Product product = productRepository.getReferenceById(idProduct);

        List<Long> newSizes = sizes.stream().map(Size::getId).toList();
        List<Long> oldSizes = product.getSizes().stream().map(Size::getId).toList();

        for (Long sizeId : oldSizes) {
            if (!newSizes.contains(sizeId)) {
                product.removeSize(sizeId);
            }
        }

    }


    @Override
    public ProductDto findById(Long id) {
        return convertToDto(productRepository.getById(id));
    }

    @Override
    public Optional<Product> getById(Long id) {
        return productRepository.findById(id);
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
    public Page<ProductDto> pageProduct(int pageNo) {
        Pageable pageable = PageRequest.of(pageNo, 8);
        List<ProductDto> dtoList = convertToDtoList(productRepository.findAll());
        return toPage(pageable, dtoList);
    }

    @Override
    public Page<ProductDto> pageProductSearch(String keyword, int pageNo) {
        Pageable pageable = PageRequest.of(pageNo, 8);
        List<ProductDto> dtoList = convertToDtoList(productRepository.findByKeyword(keyword));
        return toPage(pageable, dtoList);
    }

    private Page<ProductDto> toPage(Pageable pageable, List<ProductDto> list) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), list.size());
        return new PageImpl<ProductDto>(list.subList(start, end), pageable, list.size());
    }
}
