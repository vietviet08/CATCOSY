package com.catcosy.library.service;

import com.catcosy.library.model.Cart;
import com.catcosy.library.model.Customer;
import com.catcosy.library.model.Product;
import com.catcosy.library.model.Size;

public interface CartService {


    void addItemToCard(Product product, int quantity, Size size, Customer customer);

    void deleteCartItem(Product product, Customer customer);

    void updateCartItem(Product product, int quantity, Size size, Customer customer);

    void deleteAllCartItem(Customer customer);

    Cart updateQuantity(Long idCart, Long idCartItem, int quantity);

    Cart deleteCartItem(Long idCart, Long idCartItem);

}
