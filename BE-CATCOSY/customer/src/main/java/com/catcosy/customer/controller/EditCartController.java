package com.catcosy.customer.controller;

import com.catcosy.library.model.Cart;
import com.catcosy.library.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart")
public class EditCartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/update-quantity")
    public ResponseEntity<Double> updateQuantity(@RequestParam("idCart") Long idCart,
                                                 @RequestParam("idCartItem") Long idCartItem,
                                                 @RequestParam("quantity") int quantity) {
        Cart cart = cartService.updateQuantity(idCart, idCartItem, quantity);
        return ResponseEntity.ok(cart.getTotalPrice());
    }

    @PostMapping("/delete-cartItem")
    public ResponseEntity<Cart> deleteCartItem(@RequestParam("idCart") Long idCart,
                                               @RequestParam("idCartItem") Long idCartItem) {
        Cart cart = cartService.deleteCartItem(idCart, idCartItem);
        return ResponseEntity.ok(cart);
    }
}
