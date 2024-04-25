package com.dacs1.library.repository;

import com.dacs1.library.model.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    @Modifying
    @Query(value = "delete from orders_detail where order_id = :id", nativeQuery = true)
    void deleteAllByOrderId(@Param("id") Long id);
}
