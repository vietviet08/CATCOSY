package com.dacs1.library.repository;

import com.dacs1.library.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    @Query("select pi from ProductImage pi where pi.product.id = :idProduct")
    List<ProductImage> findByIdProduct(@Param("idProduct") Long idProduct);

    @Query("select distinct pi from ProductImage pi group by pi.product.id")
    List<ProductImage> findByIdProductUnique();

    @Query("select distinct pi from ProductImage pi where pi.product.id = :idP group by pi.product.id")
    List<ProductImage> findByIdProductUnique(@Param("idP") Long idP);


}
