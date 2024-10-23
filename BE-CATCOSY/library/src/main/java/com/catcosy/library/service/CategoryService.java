package com.catcosy.library.service;

import com.catcosy.library.model.Category;

import java.util.List;
import java.util.Optional;


public interface CategoryService {


    List<Category> findAllCategory();

    List<Category> findAllCategoryIsActivate();

    Optional<Category> findById(Long id);

    Category getById(Long id);

    Category save(Category category);

    Category update(Category category);

    void activatedById(Long id);


    void deleteById(Long id);

}
