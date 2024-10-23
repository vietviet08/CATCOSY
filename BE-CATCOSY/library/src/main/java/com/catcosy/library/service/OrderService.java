package com.catcosy.library.service;

import com.catcosy.library.dto.OrderDto;
import com.catcosy.library.model.Cart;
import com.catcosy.library.model.Customer;
import com.catcosy.library.model.Order;

import java.text.ParseException;
import java.util.List;

public interface OrderService {


    Order addOrder(Cart cart, Order order) throws ParseException;

    List<Order> findAllOrder();

    List<Order> findAllOrderByIdDesc();

    List<Order> finAllOrderByCustomerId(Customer customer);

    Order findOrderById(Long id);

    OrderDto findOrderByIdDto(Long id);

    Order findOrderByCodeViewOrder(String code);

    void deleteOrder(Long id);

    Order acceptOrder(Long id);

    Order cancelOrder(Long id);

    Order cancelOrderForCustomer(Long id);

    Order changeStatusAndNote(Long id, int statusValue, String notes);

}
