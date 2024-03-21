package com.dacs1.library.repository;

import com.dacs1.library.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {


    @Query("select p from  Product  p where p.name like %?1% or p.description like %?1%")
    List<Product> findByKeyword(String keyword);
}
