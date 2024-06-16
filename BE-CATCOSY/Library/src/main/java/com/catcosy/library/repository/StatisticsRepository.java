package com.catcosy.library.repository;

import com.catcosy.library.model.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StatisticsRepository extends JpaRepository<Statistics, Long> {

    @Query(value = "select count(username) from customers", nativeQuery = true)
    int totalCustomer ();

    @Query(value = "select count(*) from orders where status = 'Goods received'", nativeQuery = true)
    int totalOrdersDelivered();

    @Query(value = "select sum(total_price) from orders where status = 'Goods received'", nativeQuery = true)
    double totalPrice();

    @Query(value = "select  count(*) from products", nativeQuery = true)
    int totalProduct();

    @Query(value = "SELECT count(product_id) AS total FROM products AS p\n" +
            "INNER JOIN categories AS c\n" +
            "ON c.category_id = p.category_id\n" +
            "WHERE c.name = :nameCategory", nativeQuery = true)
    int totalProductByCategory(@Param("nameCategory") String nameCategory);



}
