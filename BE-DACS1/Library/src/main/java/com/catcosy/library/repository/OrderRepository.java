package com.catcosy.library.repository;

import com.catcosy.library.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query(value = "select * from orders order by order_id desc", nativeQuery = true)
    List<Order> findAllOrderByIdDesc();

    @Query(value = "select * from orders o where o.customer_id = :id and is_cancel = 0", nativeQuery = true)
    List<Order> findAllByCustomerId(@Param("id") Long id);

    @Query(value = "select * from orders o where o.code_view_order = :code and o.is_accept = 1", nativeQuery = true)
    Order findOrderByCodeViewOrder(String code);

    @Query(value = "select * from orders o where o.order_id = :id and is_accept = 1", nativeQuery = true)
    Order checkAcceptAdmin(Long id);

    @Query(value = "delete from orders where customer_id = :id", nativeQuery = true)
    @Modifying
    void deleteAllByIDCustomer(@Param("id") Long id);


}
