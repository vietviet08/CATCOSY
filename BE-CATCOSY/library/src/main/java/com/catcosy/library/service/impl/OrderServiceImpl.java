package com.catcosy.library.service.impl;

import com.catcosy.library.model.*;
import com.catcosy.library.repository.OrderDetailRepository;
import com.catcosy.library.repository.OrderRepository;
import com.catcosy.library.dto.OrderDto;
import com.catcosy.library.service.CartService;
import com.catcosy.library.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    public Order addOrder(Cart cart, Order order) throws ParseException {
        Set<CartItem> items = cart.getItems();

        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 3);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

        Date formattedDateFrom = formatter.parse(formatter.format(date));
        order.setOrderDate(formattedDateFrom);

        Date formattedDateTo = formatter.parse(formatter.format(calendar.getTime()));
        order.setDeliveryDate(formattedDateTo);


        order.setCustomer(cart.getCustomer());

        order.setTotalPrice(cart.getTotalPrice());
        order.setDiscountPrice(0.0);
        order.setShippingFee(0.0);

        UUID uuid = UUID.randomUUID();
        order.setCodeViewOrder(uuid.toString());

        order.setAccept(false);
        order.setStatus(status[0]);


        List<OrderDetail> orderDetails = new ArrayList<>();

        for (CartItem item : items) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(item.getProduct());
            orderDetail.setQuantity(item.getQuantity());
            orderDetail.setTotalPrice(item.getTotalPrice());
            orderDetail.setUnitPrice(item.getUnitPrice());
            orderDetail.setSize(item.getSize());
            orderDetail.setAllowComment(false);
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
    public List<Order> findAllOrderByIdDesc() {
        return orderRepository.findAllOrderByIdDesc();
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
    public OrderDto findOrderByIdDto(Long id) {
        Order order = orderRepository.getReferenceById(id);
        return new OrderDto(order.getId(), order.getStatus(), order.getNotes());
    }

    @Override
    public Order findOrderByCodeViewOrder(String code) {
        return orderRepository.findOrderByCodeViewOrder(code);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        orderDetailRepository.deleteAllByOrderId(id);
        orderRepository.deleteById(id);
    }

    @Override
    public Order acceptOrder(Long id) {
        Order order = orderRepository.getReferenceById(id);
        order.setAccept(true);
        order.setStatus(status[1]);
        return orderRepository.save(order);
    }

    @Override
    public Order cancelOrder(Long id) {
        Order order = orderRepository.getReferenceById(id);
        order.setAccept(false);
        order.setStatus(status[0]);
        return orderRepository.save(order);
    }

    @Override
    public Order cancelOrderForCustomer(Long id) {

        Order check = orderRepository.checkAcceptAdmin(id);
        if (check != null && check.isAccept()) return null;

        Order order = orderRepository.getReferenceById(id);
        order.setCancel(true);

        order.setStatus(status[3]);

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        String strDate = formatter.format(date);

        order.setNotes("The customer canceled the order at " + strDate);

        return orderRepository.save(order);
    }

    @Override
    public Order changeStatusAndNote(Long id, int statusValue, String notes) {
        Order order = orderRepository.getReferenceById(id);
        order.setStatus(status[statusValue]);


        if (statusValue == 2) {
            List<OrderDetail> orderDetails = order.getOrderDetails();
            orderDetails.forEach(orderDetail -> {
                orderDetail.setAllowComment(true);
//                orderDetailRepository.save(orderDetail);
            });
        }

        order.setNotes(notes);
        return orderRepository.save(order);
    }


}
