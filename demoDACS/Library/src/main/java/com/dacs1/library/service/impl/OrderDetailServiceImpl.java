package com.dacs1.library.service.impl;

import com.dacs1.library.dto.OrderDetailDto;
import com.dacs1.library.model.OrderDetail;
import com.dacs1.library.repository.OrderDetailRepository;
import com.dacs1.library.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class OrderDetailServiceImpl implements OrderDetailService {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Override
    public List<OrderDetail> findAllByOrderId(Long id) {
        return orderDetailRepository.findAllByOrderId(id);
    }

    @Override
    public List<OrderDetailDto> finAllByOrderIdDto(Long id) {
        return toListDto(orderDetailRepository.findAllByOrderId(id));

    }

    @Override
    public boolean checkAllowComment(Long idCustomer, Long idProduct) {
        AtomicBoolean allow = new AtomicBoolean(false);

        List<OrderDetail> orderDetails = orderDetailRepository.orderDetailAllowComment(idCustomer, idProduct);
        orderDetails.forEach(orderDetail -> {
            if (orderDetail.isAllowComment()) {
                allow.set(true);
            }
        });

        return allow.get();
    }

    private List<OrderDetailDto> toListDto(List<OrderDetail> orderDetails) {
        List<OrderDetailDto> orderDetailDtos = new ArrayList<>();

        for (OrderDetail orderDetail : orderDetails) {
            OrderDetailDto orderDetailDto = new OrderDetailDto();
            orderDetailDto.setId(orderDetail.getId());
            orderDetailDto.setImage(orderDetail.getProduct().getImages().get(0).getImage());
            orderDetailDto.setIdProduct(orderDetail.getProduct().getId());
            orderDetailDto.setNameProduct(orderDetail.getProduct().getName());
            orderDetailDto.setUnitPrice(orderDetail.getUnitPrice());
            orderDetailDto.setQuantityAndSize(orderDetail.getSize().getSize() + " x" + orderDetail.getQuantity());
            orderDetailDto.setTotalPrice(orderDetail.getTotalPrice());
            orderDetailDto.setAllowComment(orderDetail.isAllowComment());
            orderDetailDtos.add(orderDetailDto);
        }

        return orderDetailDtos;


    }
}
