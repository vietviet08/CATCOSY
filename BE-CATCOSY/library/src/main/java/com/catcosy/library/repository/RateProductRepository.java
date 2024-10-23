package com.catcosy.library.repository;

import com.catcosy.library.model.RateProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RateProductRepository extends JpaRepository<RateProduct, Long> {


//    @Query(value = "select * fro", nativeQuery = true)
//    void checkAllowComment(@Param("") Long idCustomer, @Param("") Long idOrderDetail);

    @Query(value = "select * from comments c where c.product_id = :idProduct ", nativeQuery = true)
    List<RateProduct> getAllByIdProduct(@Param("idProduct") Long idProduct);

    @Query(value = "select * from comments c where c.product_id = :idProduct and c.is_delete = false", nativeQuery = true)
    List<RateProduct> getAllByIdProductAndEnable(@Param("idProduct") Long idProduct);

}
