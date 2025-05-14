package com.catcosy.library.service.impl;

import com.catcosy.library.repository.OrderDetailRepository;
import com.catcosy.library.dto.OrderDetailDto;
import com.catcosy.library.model.OrderDetail;
import com.catcosy.library.model.ProductImage;
import com.catcosy.library.service.OrderDetailService;
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
            
            // Safely handle product images - check if product or images are null
            if (orderDetail.getProduct() != null && 
                orderDetail.getProduct().getImages() != null && 
                !orderDetail.getProduct().getImages().isEmpty()) {
                ProductImage firstImage = orderDetail.getProduct().getImages().get(0);
                orderDetailDto.setImage(firstImage.getS3Url());
            } else {
                orderDetailDto.setImage(null); // Set default or placeholder image URL if needed
            }
            
            // Set product info safely
            if (orderDetail.getProduct() != null) {
                orderDetailDto.setIdProduct(orderDetail.getProduct().getId());
                orderDetailDto.setNameProduct(orderDetail.getProduct().getName());
            }
            
            orderDetailDto.setUnitPrice(orderDetail.getUnitPrice());
            
            // Set size safely
            if (orderDetail.getSize() != null) {
                orderDetailDto.setQuantityAndSize(orderDetail.getSize().getSize() + " x" + orderDetail.getQuantity());
            } else {
                orderDetailDto.setQuantityAndSize("x" + orderDetail.getQuantity());
            }
            
            orderDetailDto.setTotalPrice(orderDetail.getTotalPrice());
            orderDetailDto.setAllowComment(orderDetail.isAllowComment());
            orderDetailDtos.add(orderDetailDto);
        }

        return orderDetailDtos;
    }
}
