package com.dacs1.library.service.impl;

import com.dacs1.library.dto.ProductDto;
import com.dacs1.library.model.Product;
import com.dacs1.library.model.ProductImage;
import com.dacs1.library.model.ProductSize;
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
    public Product save(List<MultipartFile> images, List<Long> sizes, ProductDto productDto) throws IOException {
        Product product = new Product();


        try {
            product.setId(productDto.getId());
            product.setName(productDto.getName());
            product.setDescription(productDto.getDescription());
            product.setCostPrice(productDto.getCostPrice());
            product.setSalePrice((double) 0);
            product.setQuantity(0);
            product.setCategory(productDto.getCategory());
            product.setIsActivated(true);
            product.setIsDeleted(false);

            List<ProductImage> imageList = new ArrayList<>();
            for (MultipartFile image : images) {
                ProductImage productImage = new ProductImage();
                String fileImg = Base64.getEncoder().encodeToString(image.getBytes());
                productImage.setProduct(product);
                System.out.println(product.toString());
                productImage.setImage(fileImg);
                imageList.add(productImage);
            }

            List<ProductSize> productSizes = new ArrayList<>();
            for (Long id : sizes) {
                ProductSize productSize = new ProductSize();
                Size size = sizeRepository.getReferenceById(id);
                productSize.setSize(size);
                productSize.setQuantity(0);
                System.out.println(product);
                productSize.setProduct(product);
                productSizes.add(productSize);
            }

            product.setImages(imageList);
            product.setSizes(productSizes);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return productRepository.save(product);
    }

    private void setIdForProduct(){

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
                    productImage.setProduct(null);
                }

                productImages.add(productImage);
            }
            i++;
        }
        if (!productImages.isEmpty()) product.setImages(productImages);
        else product.setImages(productDto.getImages());

        product.setId(productDto.getId());
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setSizes(productDto.getSizes());
        product.setCostPrice(productDto.getCostPrice());
        product.setSalePrice(productDto.getSalePrice());
        product.setQuantity(productDto.getQuantity());
        product.setCategory(productDto.getCategory());

        return productRepository.save(product);
    }

    @Override
    public void updateProductSize(Long idProduct, List<ProductSize> sizes) {

        Product product = productRepository.getReferenceById(idProduct);


        List<Long> newSizes = sizes.stream().map(size -> size.getSize().getId()).toList();
        List<Long> oldSizes = product.getSizes().stream().map(size -> size.getSize().getId()).toList();

        for (Long sizeId : oldSizes) {
            if (!newSizes.contains(sizeId)) {
                removeSize(sizeId, product);
            }
        }

    }

    public void removeSize(Long id, Product product) {
        List<ProductSize> sizes = product.getSizes();
        for (ProductSize size : sizes) {
            if (size.getSize().getId().equals(id)) {
                sizes.remove(size);
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

    private Page<ProductDto> toPage(Pageable pageable, List<ProductDto> list) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), list.size());
        return new PageImpl<ProductDto>(list.subList(start, end), pageable, list.size());
    }
}
