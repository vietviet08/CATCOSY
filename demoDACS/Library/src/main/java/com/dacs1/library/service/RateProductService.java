package com.dacs1.library.service;

import com.dacs1.library.model.RateProduct;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RateProductService {

    RateProduct addComment(Long idProduct, String username, int star, String content, List<MultipartFile> images);

    RateProduct editComment(RateProduct comment);
    void deleteComment(Long idComment, String username);

    RateProduct likeComment(Long idComment, String  username);

    RateProduct answerComment(Long idCommentCustomer);

}
