package com.catcosy.library.repository;

import com.catcosy.library.model.RateProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RateProductImageRepository extends JpaRepository<RateProductImage, Long> {

    @Query("SELECT r FROM RateProductImage r WHERE r.rateProduct.id = :rateId")
    List<RateProductImage> findAllByRateId(@Param("rateId") Long rateId);
}
