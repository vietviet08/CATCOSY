package com.catcosy.library.service;

import com.catcosy.library.model.RateProduct;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RateProductService {

    RateProduct addComment(Long idProduct, Long idOrderDetail, String username, int star, String content, List<MultipartFile> images);

    RateProduct editComment(RateProduct comment);

    void deleteComment(Long idComment, String username);

    void deleteCommentByAdmin(Long idComment);

    void allowCommentByAdmin(Long idComment);

    RateProduct likeComment(Long idComment, String username);

    RateProduct answerComment(Long idCommentCustomer);

    RateProduct getByIdComment(Long idComment);

    List<RateProduct> getAllByIdProduct(Long idProduct);

    List<RateProduct> getAllByIdProductAndEnable(Long idProduct);

    boolean checkLikedComment(String username, Long idComment);

}
