package com.catcosy.library.repository;

import com.catcosy.library.model.RateProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RateProductImageRepository extends JpaRepository<RateProductImage, Long> {
}
