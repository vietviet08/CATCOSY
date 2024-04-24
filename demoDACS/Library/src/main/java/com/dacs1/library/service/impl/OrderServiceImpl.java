package com.dacs1.library.service.impl;

import com.dacs1.library.model.*;
import com.dacs1.library.repository.CartRepository;
import com.dacs1.library.repository.OrderDetailRepository;
import com.dacs1.library.repository.OrderRepository;
import com.dacs1.library.service.CartService;
import com.dacs1.library.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private CartService cartService;

    private final String[] status = {"Awaiting approval", "Delivering", "Goods received", "Cancelled"};

    @Override
    public Order addOrder(Cart cart, Order order) {
        Set<CartItem> items = cart.getItems();

        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 3);

        order.setOrderDate(date);
        order.setDeliveryDate(calendar.getTime());

        order.setCustomer(cart.getCustomer());

        order.setTotalPrice(cart.getTotalPrice());
        order.setShippingFee(0.0);
        order.setAccept(false);
        order.setStatus(status[0]);

        List<OrderDetail> orderDetails = new ArrayList<>();

        for(CartItem item : items){
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(item.getProduct());
            orderDetail.setQuantity(item.getQuantity());
            orderDetail.setTotalPrice(item.getTotalPrice());
            orderDetail.setUnitPrice(item.getUnitPrice());
            orderDetailRepository.save(orderDetail);
            orderDetails.add(orderDetail);
        }

        order.setOrderDetails(orderDetails);

        cartService.deleteAllCartItem(cart.getCustomer());

        return orderRepository.save(order);
    }

    @Override
    public List<Order> findAllOrder() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> finAllOrderByCustomerId(Customer customer) {
        return orderRepository.findAllByCustomerId(customer.getId());
    }

    @Override
    public Order findOrderById(Long id) {
        return orderRepository.getReferenceById(id);
    }

    @Override
    public void deleteOrder(Long id) {
         orderRepository.deleteById(id);
    }

    @Override
    public Order acceptOrder(Long id) {
        return orderRepository.acceptOder(id);
    }

    @Override
    public Order changeStatus(Long id) {
        return null;
    }
}
