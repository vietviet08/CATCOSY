package com.dacs1.library.service.impl;

import com.dacs1.library.model.*;
import com.dacs1.library.repository.CartItemRepository;
import com.dacs1.library.repository.CartRepository;
import com.dacs1.library.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Override
    @Transactional
    public void addItemToCard(Product product, int quantity, Size size, Customer customer) {
        if(quantity <=0 ) return;

        Cart cart = customer.getCart();
        if (cart == null) {
            cart = new Cart();
            cart.setCustomer(customer);
            cart.setTotalItem(0);
            cart.setTotalPrice(0.0);
        }

        Set<CartItem> cartItems = cart.getItems();
        CartItem cartItem = findCartItem(product.getId(), cartItems);

        if (cartItems == null) {
            cartItems = new HashSet<>();
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setSize(size);
            cartItem.setUnitPrice(product.getCostPrice());
            cartItem.setTotalPrice(product.getCostPrice() * quantity);
            cartItems.add(cartItem);
            cartItemRepository.save(cartItem);
            cart.setTotalItem(cart.getTotalItem() + 1);
        } else {
            if (cartItem == null || cartItem.getSize().getId() != size.getId()) {
                cartItem = new CartItem();
                cartItem.setCart(cart);
                cartItem.setProduct(product);
                cartItem.setQuantity(quantity);
                cartItem.setSize(size);
                cartItem.setUnitPrice(product.getCostPrice());
                cartItem.setTotalPrice(product.getCostPrice() * quantity);
                cartItems.add(cartItem);
                cartItemRepository.save(cartItem);
                cart.setTotalItem(cart.getTotalItem() + 1);
            } else {
                cartItem.setQuantity(cartItem.getQuantity() + quantity);
                cartItem.setSize(size);
                cartItem.setTotalPrice(cartItem.getTotalPrice() + product.getCostPrice() * quantity);
                cartItemRepository.save(cartItem);
            }

        }

        cart.setItems(cartItems);
        cart.setTotalPrice(cart.getTotalPrice() + cartItem.getTotalPrice());


        cartRepository.save(cart);

    }

    @Override
    @Transactional
    public void deleteCartItem(Product product, Customer customer) {

        Cart cart = customer.getCart();

        Set<CartItem> items = cart.getItems();

        CartItem cartItem = null;

        for (CartItem item : items) {
            if (Objects.equals(item.getProduct().getId(), product.getId())){
                cartItem = item;
                break;
            }
        }
        cartItemRepository.removeCartItem(cartItem.getId());
        items.remove(cartItem);
        cart.setItems(items);
        cart.setTotalPrice(cart.getTotalPrice() - cartItem.getTotalPrice());
        cart.setTotalItem(cart.getTotalItem() - 1);


        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void updateCartItem(Product product, int quantity, Size size, Customer customer) {
        if(quantity <=0 ) return;

        Cart cart = customer.getCart();
        Set<CartItem> items = cart.getItems();


        for (CartItem item : items) {
            if (item.getProduct().getId() == product.getId()) {
                if(item.getQuantity() - quantity <= 0){
                    deleteCartItem(product, customer);
                    return;
                }
                item.setQuantity(quantity);
                item.setTotalPrice(item.getTotalPrice() - quantity * item.getUnitPrice());
                item.setSize(size);
                cart.setTotalPrice(cart.getTotalPrice() - quantity * item.getUnitPrice());
                cartItemRepository.save(item);
            }
        }

        cartRepository.save(cart);

    }

    private CartItem findCartItem(Long idProduct, Set<CartItem> items) {
        if (items == null) return null;

        CartItem cartItem = null;

        for (CartItem item : items) {
            if (Objects.equals(item.getProduct().getId(), idProduct)) {
                cartItem = item;
            }

        }

        return cartItem;
    }

    private double getTotalPriceCartItems(Set<CartItem> items) {
        double total = 0.0;

        for (CartItem item : items)
            total += item.getTotalPrice();

        return total;
    }

    private int getTotalItem(Set<CartItem> items) {
        int totalItem = 0;

        for (CartItem item : items)
            totalItem++;
        return totalItem;
    }
}
