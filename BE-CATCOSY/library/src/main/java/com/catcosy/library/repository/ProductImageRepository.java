package com.catcosy.library.repository;
import com.catcosy.library.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    @Query("select pi from ProductImage pi where pi.product.id = :idProduct")
    List<ProductImage> findByIdProduct(@Param("idProduct") Long idProduct);
    
    @Query("select pi from ProductImage pi where pi.id in (select min(p.id) from ProductImage p group by p.product.id)")
    List<ProductImage> findByIdProductUnique();
    
    @Query("select pi from ProductImage pi where pi.product.id = :idP and pi.id = (select min(p.id) from ProductImage p where p.product.id = :idP)")
    List<ProductImage> findByIdProductUnique(@Param("idP") Long idP);
    
    @Modifying
    @Query(value = "delete from products_images where product_id = :id", nativeQuery = true)
    void deleteAllByProductId(@Param("id") Long id);
}
