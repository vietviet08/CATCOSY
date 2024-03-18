package com.dacs1.library.service;

import com.dacs1.library.model.Category;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


public interface CategoryService {


    List<Category> findAllCategory();

    List<Category> findAllCategoryIsActivate();

    Optional<Category> findById(Long id);


    Category save(Category category);

    Category update(Category category);

    void activatedById(Long id);


    void deleteById(Long id);

}
