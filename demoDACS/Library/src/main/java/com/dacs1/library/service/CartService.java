package com.dacs1.library.service;

import com.dacs1.library.model.Cart;
import com.dacs1.library.model.CartItem;
import com.dacs1.library.model.Customer;
import com.dacs1.library.model.Product;

public interface CartService {


    Cart addItemToCard(Product product, int quantity, Customer customer);

}
