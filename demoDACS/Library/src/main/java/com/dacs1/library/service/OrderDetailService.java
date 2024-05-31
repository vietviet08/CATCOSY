package com.dacs1.library.service;

import com.dacs1.library.dto.OrderDetailDto;
import com.dacs1.library.model.OrderDetail;

import java.util.List;

public interface OrderDetailService {

    List<OrderDetail> findAllByOrderId(Long id);

    List<OrderDetailDto> finAllByOrderIdDto(Long id);

    boolean checkAllowComment(Long idCustomer, Long idProduct);
}
