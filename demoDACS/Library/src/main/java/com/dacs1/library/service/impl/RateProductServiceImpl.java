package com.dacs1.library.service.impl;

import com.dacs1.library.model.CustomerLikedComment;
import com.dacs1.library.model.RateProduct;
import com.dacs1.library.model.RateProductImage;
import com.dacs1.library.repository.*;
import com.dacs1.library.service.RateProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class RateProductServiceImpl implements RateProductService {

    @Autowired
    private RateProductRepository rateProductRepository;

    @Autowired
    private CustomerLikedCommentRepository customerLikedCommentRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    RateProductImageRepository rateProductImageRepository;


    @Override
    public RateProduct addComment(Long idProduct, String username, int star, String content, List<MultipartFile> images) {
        RateProduct rateProduct = new RateProduct();
        rateProduct.setProduct(productRepository.getByIdProduct(idProduct));
        rateProduct.setCustomer(customerRepository.findByUsername(username));
        rateProduct.setRated(true);
        rateProduct.setStar(star);
        rateProduct.setContent(content);
        rateProduct.setDelete(false);
        rateProduct.setAmountOfLike(0);
        rateProduct.setDateUpload(LocalDateTime.now());

//         rateProductRepository.save(rateProduct);

        List<RateProductImage> rateProductImages = new ArrayList<>();

        images.forEach(file -> {
            RateProductImage rateProductImage = new RateProductImage();
            try {
                rateProductImage.setRateProduct(rateProduct);
                rateProductImage.setImage(Base64.getEncoder().encodeToString(file.getBytes()));
//                rateProductImageRepository.save(rateProductImage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            rateProductImages.add(rateProductImage);
        });

        rateProduct.setImages(rateProductImages);

        return rateProductRepository.save(rateProduct);

    }

    @Override
    public RateProduct editComment(RateProduct comment) {
        return rateProductRepository.save(comment);
    }

    @Override
    public void deleteComment(Long idComment, String username) {

        RateProduct rateProduct = rateProductRepository.getReferenceById(idComment);
        if (!rateProduct.getCustomer().getUsername().equals(username)) return;
        rateProduct.setDelete(true);
        rateProductRepository.save(rateProduct);
    }

    @Override
    public RateProduct likeComment(Long idComment, String username) {
        RateProduct comment = rateProductRepository.getReferenceById(idComment);
        if (Objects.equals(comment.getCustomer().getUsername(), username)) return null;
        List<CustomerLikedComment> customerLikedComments = comment.getCustomersLikedComment();
        AtomicBoolean check = new AtomicBoolean(false);
        if (customerLikedComments == null || customerLikedComments.isEmpty()) {
            customerLikedComments = new ArrayList<>();
            CustomerLikedComment customerLikedCommentNew = new CustomerLikedComment();
            customerLikedCommentNew.setCustomer(customerRepository.getReferenceById(idComment));
            customerLikedCommentNew.setLiked(true);
            customerLikedCommentNew.setRateProduct(comment);
            customerLikedComments.add(customerLikedCommentNew);
//            customerLikedCommentRepository.save(customerLikedCommentNew);
            comment.setCustomersLikedComment(customerLikedComments);
            comment.setAmountOfLike(comment.getAmountOfLike() + 1);

        } else customerLikedComments.forEach(customerLikedComment -> {

            if (Objects.equals(customerLikedComment.getCustomer().getUsername(), username)) {
                if (customerLikedComment.isLiked()) {
                    customerLikedComment.setLiked(false);
//                    customerLikedCommentRepository.save(customerLikedComment);
                    comment.setAmountOfLike(comment.getAmountOfLike() + 1);
                } else {
                    customerLikedComment.setLiked(true);
//                    customerLikedCommentRepository.save(customerLikedComment);
                    comment.setAmountOfLike(comment.getAmountOfLike() - 1);
                }
                check.set(true);
            }
        });

        if (!check.get()) {
            CustomerLikedComment customerLikedCommentNew = new CustomerLikedComment();
            customerLikedCommentNew.setCustomer(customerRepository.getReferenceById(idComment));
            customerLikedCommentNew.setLiked(true);
            customerLikedCommentNew.setRateProduct(comment);
            customerLikedComments.add(customerLikedCommentNew);
//            customerLikedCommentRepository.save(customerLikedCommentNew);
            comment.setAmountOfLike(comment.getAmountOfLike() + 1);
        }

        return rateProductRepository.save(comment);
    }

    @Override
    public RateProduct answerComment(Long idCommentCustomer) {
        return null;
    }
}
