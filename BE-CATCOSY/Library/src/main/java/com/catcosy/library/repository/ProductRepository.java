package com.catcosy.library.repository;

import com.catcosy.library.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("SELECT p FROM Product p " +
            "INNER JOIN Category c ON c.id = p.category.id WHERE p.isActivated = true AND c.isActivated = true " +
            "AND (:size IS NULL OR 0 in :size OR p.id IN (select s.product.id from ProductSize s where s.size.id in :size)) " +
            "AND (:idCategory IS NULL OR :idCategory = 0 OR p.category.id = :idCategory) " +
            "AND (:keyword IS NULL OR p.name LIKE %:keyword%)" +
            "AND ((:minPrice IS NULL AND :maxPrice IS NULL) OR (p.costPrice BETWEEN :minPrice AND :maxPrice)) ORDER BY " +
            "CASE WHEN :sortPrice = 'true' THEN p.costPrice END ASC, " +
            "CASE WHEN :sortPrice = 'false' THEN p.costPrice END DESC," +
            "CASE WHEN :sortPrice = 'news' THEN p.id END DESC")
    List<Product> findAllIsActivatedFilter(@Param("keyword") String keyword,
                                           @Param("sortPrice") String sortPrice,
                                           @Param("idCategory") Long idCategory,
                                           @Param("minPrice") Integer minPrice,
                                           @Param("maxPrice") Integer maxPrice,
                                           @Param("size") List<Long> size);

    @Query("select p from Product p inner join Category c on c.id = p.category.id where  p.isActivated = true and c.isActivated = true and p.name like %?1%")
    List<Product> findAllIsActivatedFilterSearch(String keyword);

    @Query("select p from Product p inner join Category c on c.id = p.category.id where  p.isActivated = true and c.isActivated = true and p.name like %?1% order by p.costPrice")
    List<Product> findAllIsActivatedFilterSortPrice(String keyword);


    @Query(value = "select * from  products p order by rand() limit :limitProduct", nativeQuery = true)
    List<Product> findAllRandomProduct(@Param("limitProduct") int limitProduct);

    @Query(value = "select * from  products p where p.sale_price > 0 order by rand() limit :limitProduct", nativeQuery = true)
    List<Product> findAllRandomProductSale(@Param("limitProduct") int limitProduct);


    @Query(value = "select * from products p where p.category_id = :idCategory and p.product_id <> :idProduct order by rand() limit :limitProduct", nativeQuery = true)
    List<Product> findAllProductSameCategory(@Param("idCategory") Long idCategory,
                                             @Param("idProduct") Long idProduct,
                                             @Param("limitProduct") int limitProduct);


}
