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

    @Query("select p from Product p inner join Category c on c.id = p.category.id where  p.isActivated = true and c.isActivated = true")
    List<Product> findAllIsActivated();

    @Query("SELECT p FROM Product p INNER JOIN Category c ON c.id = p.category.id WHERE p.isActivated = true AND c.isActivated = true " +
            "AND (:idCategory IS NULL OR :idCategory = 0 OR p.category.id = :idCategory) " +
            "AND (:keyword IS NULL OR p.name LIKE %:keyword%) ORDER BY " +
            "CASE WHEN :sortPrice = 'true' THEN p.costPrice END ASC, " +
            "CASE WHEN :sortPrice = 'false' THEN p.costPrice END DESC," +
            "CASE WHEN :sortPrice = 'news' THEN p.id END DESC")
    List<Product> findAllIsActivatedFilter(@Param("keyword") String keyword, @Param("sortPrice") String sortPrice, @Param("idCategory") Long idCategory);

    @Query("select p from Product p inner join Category c on c.id = p.category.id where  p.isActivated = true and c.isActivated = true and p.name like %?1%")
    List<Product> findAllIsActivatedFilterSearch(String keyword);

    @Query("select p from Product p inner join Category c on c.id = p.category.id where  p.isActivated = true and c.isActivated = true and p.name like %?1% order by p.costPrice")
    List<Product> findAllIsActivatedFilterSortPrice(String keyword);


    @Query(value = "select * from  products p order by rand() limit :limitProduct", nativeQuery = true)
    List<Product> findAllRandomProduct(@Param("limitProduct") int limitProduct);

    @Query(value = "select * from products p where p.category_id = :idCategory and p.product_id <> :idProduct ", nativeQuery = true)
    List<Product> findAllProductSameCategory(@Param("idCategory") Long idCategory,
                                             @Param("idProduct") Long idProduct);


}
