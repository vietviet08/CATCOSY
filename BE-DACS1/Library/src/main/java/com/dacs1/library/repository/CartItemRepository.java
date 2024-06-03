package com.dacs1.library.repository;

import com.dacs1.library.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Modifying
    @Query(value = "delete from CartItem ci where ci.id =:id")
    void removeCartItem(@Param("id") Long id);

    @Query(value = "delete from cart_items ci where ci.cart_id := id", nativeQuery = true)
    void removeCartItemByCartId(@Param("id") Long id);

}
