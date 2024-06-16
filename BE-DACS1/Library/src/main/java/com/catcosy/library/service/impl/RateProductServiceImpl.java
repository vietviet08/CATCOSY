package com.catcosy.library.service.impl;

import com.catcosy.library.repository.*;
import com.catcosy.library.model.CustomerLikedComment;
import com.catcosy.library.model.OrderDetail;
import com.catcosy.library.model.RateProduct;
import com.catcosy.library.model.RateProductImage;
import com.dacs1.library.repository.*;
import com.catcosy.library.service.RateProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

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

    @Autowired
    OrderDetailRepository orderDetailRepository;


    @Override
    public RateProduct addComment(Long idProduct, Long idOrderDetail, String username, int star, String content, List<MultipartFile> images) {
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

        OrderDetail orderDetail = orderDetailRepository.getReferenceById(idOrderDetail);
        orderDetail.setAllowComment(false);
        orderDetailRepository.save(orderDetail);

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
    @Transactional
    public RateProduct likeComment(Long idComment, String username) {
        RateProduct comment = rateProductRepository.getReferenceById(idComment);
        if (Objects.equals(comment.getCustomer().getUsername(), username)) return null;
        List<CustomerLikedComment> customerLikedComments = comment.getCustomersLikedComment();
        boolean check = false;
        if (customerLikedComments == null || customerLikedComments.isEmpty()) {
            customerLikedComments = new ArrayList<>();
            CustomerLikedComment customerLikedCommentNew = new CustomerLikedComment();
            customerLikedCommentNew.setCustomer(customerRepository.findByUsername(username));
            customerLikedCommentNew.setLiked(true);
            customerLikedCommentNew.setRateProduct(comment);
            customerLikedComments.add(customerLikedCommentNew);
//            customerLikedCommentRepository.save(customerLikedCommentNew);
            comment.setCustomersLikedComment(customerLikedComments);
            comment.setAmountOfLike(comment.getAmountOfLike() + 1);
            check = true;
            System.out.println("1");

        } else {
            for (CustomerLikedComment customerLikedComment : customerLikedComments) {
                if (Objects.equals(customerLikedComment.getCustomer().getUsername(), username)) {
                    if (customerLikedComment.isLiked()) {
                        customerLikedComment.setLiked(false);
//                    customerLikedCommentRepository.save(customerLikedComment);
                        comment.setAmountOfLike(comment.getAmountOfLike() - 1);
                    } else {
                        customerLikedComment.setLiked(true);
//                    customerLikedCommentRepository.save(customerLikedComment);
                        comment.setAmountOfLike(comment.getAmountOfLike() + 1);
                    }
                   check = true;
                    System.out.println("2");
                    break;
                }
            }
        }

        if (!check) {
            CustomerLikedComment customerLikedCommentNew = new CustomerLikedComment();
            customerLikedCommentNew.setCustomer(customerRepository.findByUsername(username));
            customerLikedCommentNew.setLiked(true);
            customerLikedCommentNew.setRateProduct(comment);
            customerLikedComments.add(customerLikedCommentNew);
//            customerLikedCommentRepository.save(customerLikedCommentNew);
            comment.setAmountOfLike(comment.getAmountOfLike() + 1);
            System.out.println("3");
        }

        return rateProductRepository.save(comment);
    }

    @Override
    public RateProduct answerComment(Long idCommentCustomer) {
        return null;
    }

    @Override
    public RateProduct getByIdComment(Long idComment) {
        return rateProductRepository.getReferenceById(idComment);
    }

    @Override
    public List<RateProduct> getAllByIdProduct(Long idProduct) {
        return rateProductRepository.getAllByIdProduct(idProduct);
    }

    @Override
    public boolean checkLikedComment(String username, Long idComment) {
        return customerLikedCommentRepository.checkLikedComment(customerRepository.findByUsername(username).getId(), idComment).isLiked();
    }
}
