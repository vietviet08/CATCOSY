package com.dacs1.library.repository;

import com.dacs1.library.model.Product;
import com.dacs1.library.model.ProductImage;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {


    @Query("select p from  Product  p where p.name like %?1% or p.description like %?1%")
    List<Product> findByKeyword(String keyword);

    @Query(value = "select * from products where product_id = :idProduct", nativeQuery = true)
    Product getByIdProduct(@Param("idProduct") Long idProduct);

    @Query("select p from Product p order by p.costPrice desc ")
    List<Product> findAllByPriceDesc();

    @Query("select p from Product p order by p.costPrice asc ")
    List<Product> findAllByPriceAsc();

    @Query("select p from Product p where p.category.name = :name")
    List<Product> findAllByCategory(@Param("name") String name);

    /*Customer*/

    @Query("select p from Product p where p.isDeleted = false and p.isActivated = true")
    List<Product> findAllIsActivated();


}
