package com.dacs1.library.service;

import com.dacs1.library.model.Customer;
import com.dacs1.library.model.Product;
import com.dacs1.library.model.Size;

public interface CartService {


    void addItemToCard(Product product, int quantity, Size size, Customer customer);

    void deleteCartItem(Product product, Customer customer);

    void updateCartItem(Product product, int quantity, Size size, Customer customer);

}
