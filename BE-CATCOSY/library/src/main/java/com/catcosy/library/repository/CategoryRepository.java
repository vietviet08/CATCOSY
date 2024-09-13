package com.catcosy.library.repository;

import com.catcosy.library.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("select c from Category c where c.isActivated = true and c.isDeleted = false")
    List<Category> findAllByCategoryIsActivate();
}
