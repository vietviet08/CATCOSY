package com.dacs1.library.repository;

import com.dacs1.library.model.ProductSize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductSizeRepository extends JpaRepository<ProductSize, Long> {


    @Query("select ps from ProductSize ps where ps.product.id = :idProduct")
    List<ProductSize> findAllByIdProduct(@Param("idProduct") Long idProduct);
}
