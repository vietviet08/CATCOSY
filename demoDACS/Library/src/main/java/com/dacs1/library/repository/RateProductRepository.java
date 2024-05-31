package com.dacs1.library.repository;

import com.dacs1.library.model.RateProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RateProductRepository extends JpaRepository<RateProduct, Long> {


//    @Query(value = "select * fro", nativeQuery = true)
//    void checkAllowComment(@Param("") Long idCustomer, @Param("") Long idOrderDetail);
}
