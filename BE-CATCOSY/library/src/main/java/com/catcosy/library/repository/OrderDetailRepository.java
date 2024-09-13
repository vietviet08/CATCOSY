package com.catcosy.library.repository;

import com.catcosy.library.model.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    @Modifying
    @Query(value = "delete from orders_detail where order_id = :id", nativeQuery = true)
    void deleteAllByOrderId(@Param("id") Long id);

    @Query(value = "select * from orders_detail od where od.order_id = :id ", nativeQuery = true)
    List<OrderDetail> findAllByOrderId(@Param("id") Long id);


    @Query(value =
            "select od.order_detail_id, od.quantity, od.size_id, od.total_price, od.unit_price, od.order_id, od.product_id, od.is_allow_comment from orders_detail od " +
                    " inner join orders o on o.order_id = od.order_id" +
                    " where o.customer_id = :idCustomer AND od.product_id = :idProduct ", nativeQuery = true)
    List<OrderDetail> orderDetailAllowComment(@Param("idCustomer") Long idCustomer,
                                              @Param("idProduct") Long idProduct);
}
