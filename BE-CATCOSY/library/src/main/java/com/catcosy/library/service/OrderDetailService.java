package com.catcosy.library.service;

import com.catcosy.library.dto.OrderDetailDto;
import com.catcosy.library.model.OrderDetail;

import java.util.List;

public interface OrderDetailService {

    List<OrderDetail> findAllByOrderId(Long id);

    List<OrderDetailDto> finAllByOrderIdDto(Long id);

    boolean checkAllowComment(Long idCustomer, Long idProduct);
}
