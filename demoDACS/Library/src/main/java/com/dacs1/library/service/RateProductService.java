package com.dacs1.library.service;

import com.dacs1.library.model.RateProduct;

public interface RateProductService {

    RateProduct addComment(RateProduct comment);

    RateProduct updateComment(RateProduct comment);

    RateProduct answerComment(Long idCommentCustomer);
}
