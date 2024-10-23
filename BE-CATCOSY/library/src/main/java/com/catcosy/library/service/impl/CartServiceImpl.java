package com.catcosy.library.service.impl;

import com.catcosy.library.model.*;
import com.catcosy.library.repository.CartItemRepository;
import com.catcosy.library.repository.CartRepository;
import com.catcosy.library.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

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
        if (quantity <= 0) return;

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
            double unitPrice = product.getCostPrice() - product.getSalePrice();
            cartItem.setUnitPrice(unitPrice);
            cartItem.setTotalPrice(unitPrice * quantity);
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
                double unitPrice = product.getCostPrice() - product.getSalePrice();
                cartItem.setUnitPrice(unitPrice);
                cartItem.setTotalPrice(unitPrice * quantity);
                cartItems.add(cartItem);
                cartItemRepository.save(cartItem);
                cart.setTotalItem(cart.getTotalItem() + 1);
            } else {
                cartItem.setQuantity(cartItem.getQuantity() + quantity);
                cartItem.setSize(size);
                double unitPrice = product.getCostPrice() - product.getSalePrice();
                cartItem.setTotalPrice(cartItem.getTotalPrice() + unitPrice * quantity);
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
            if (Objects.equals(item.getProduct().getId(), product.getId())) {
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
        if (quantity <= 0) return;

        Cart cart = customer.getCart();
        Set<CartItem> items = cart.getItems();


        for (CartItem item : items) {
            if (item.getProduct().getId() == product.getId()) {
                if (item.getQuantity() - quantity <= 0) {
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

    @Override
    @Transactional
    public void deleteAllCartItem(Customer customer) {
        Cart cart = customer.getCart();
        Set<CartItem> items = cart.getItems();
        if (!ObjectUtils.isEmpty(cart) && !ObjectUtils.isEmpty(cart.getItems())) {
            cartItemRepository.deleteAll(items);
        }

//cartItemRepository.removeCartItemByCartId(cart.getId());
        cart.setTotalPrice(0.0);
        cart.setTotalItem(0);
        cart.getItems().clear();
        cart.setItems(null);

        cartRepository.save(cart);
    }

    @Override
    public Cart updateQuantity(Long idCart, Long idCartItem, int quantity) {
        Cart cart = cartRepository.getReferenceById(idCart);
        for (CartItem cartItem : cart.getItems()) {
            if (Objects.equals(cartItem.getId(), idCartItem)) {
                if ( quantity <= 0) deleteCartItem(idCart, cartItem.getId());

                double priceNeed = cartItem.getUnitPrice() * quantity;
                cart.setTotalPrice(cart.getTotalPrice() - cartItem.getTotalPrice() + priceNeed);
                cartItem.setQuantity(quantity);
                cartItem.setTotalPrice(priceNeed);
                cartItemRepository.save(cartItem);
                break;
            }
        }

        return cartRepository.save(cart);
    }

    @Override
    public Cart deleteCartItem(Long idCart, Long idCartItem) {
        Cart cart = cartRepository.getReferenceById(idCart);
        CartItem cartItemNeed = null;
        double priceNeed = 0.0;


        for (CartItem cartItem : cart.getItems()) {
            if (Objects.equals(cartItem.getId(), idCartItem)) {
                cartItemNeed = cartItem;
                priceNeed = cartItem.getTotalPrice();
                break;
            }
        }

        if (cartItemNeed != null && priceNeed != 0.0) {
            cart.getItems().remove(cartItemNeed);
            cart.setTotalPrice(cart.getTotalPrice() - priceNeed);
        }

        return cartRepository.save(cart);
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
