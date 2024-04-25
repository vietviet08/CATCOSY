package com.dacs1.library.service.impl;

import com.dacs1.library.model.*;
import com.dacs1.library.repository.CartRepository;
import com.dacs1.library.repository.OrderDetailRepository;
import com.dacs1.library.repository.OrderRepository;
import com.dacs1.library.service.CartService;
import com.dacs1.library.service.OrderService;
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
        order.setShippingFee(0.0);
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

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        String strDate = formatter.format(date);

        order.setNotes("The customer canceled the order at " + strDate);

        return orderRepository.save(order);
    }


    @Override
    public Order changeStatus(Long id) {
        return null;
    }
}
