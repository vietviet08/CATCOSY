package com.dacs1.library.service;

import com.dacs1.library.model.Cart;
import com.dacs1.library.model.CartItem;
import com.dacs1.library.model.Customer;
import com.dacs1.library.model.Order;

import java.util.List;

public interface OrderService {

    Order addOrder(Cart cart, Order order);

    List<Order> findAllOrder();

    List<Order> finAllOrderByCustomerId(Customer customer);

    Order findOrderById(Long id);

    void deleteOrder(Long id);

    Order acceptOrder(Long id);

    Order changeStatus(Long id);

}
