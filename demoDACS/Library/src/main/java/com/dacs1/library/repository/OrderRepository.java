package com.dacs1.library.repository;

import com.dacs1.library.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {


    @Query(value = "select * from  orders  o where o.customer_id = :id", nativeQuery = true)
    List<Order> findAllByCustomerId(@Param("id") Long id);

    @Query(value = "update o set o.is_accept = 1 where o.order_id := id", nativeQuery = true)
    Order acceptOder(Long id);

}
