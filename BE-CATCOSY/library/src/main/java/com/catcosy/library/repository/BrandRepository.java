package com.catcosy.library.repository;

import com.catcosy.library.model.Brand;
import com.catcosy.library.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    @Query("select c from Brand c where c.isActivated = true and c.isDeleted = false")
    List<Brand> findAllByBrandIsActivate();
}
