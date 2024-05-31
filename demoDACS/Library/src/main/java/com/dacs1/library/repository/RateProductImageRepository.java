package com.dacs1.library.repository;

import com.dacs1.library.model.RateProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RateProductImageRepository extends JpaRepository<RateProductImage, Long> {
}
