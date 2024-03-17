package com.dacs1.library.service.impl;

import com.dacs1.library.model.Category;
import com.dacs1.library.repository.CategoryRepository;
import com.dacs1.library.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {


    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> findAllCategory() {
        return categoryRepository.findAll();
    }

    @Override
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public Category save(Category category){
        Category c = new Category(category.getName());
        return categoryRepository.save(c);
    }

    @Override
    public Category update(Category category) {
        Category c = categoryRepository.getReferenceById(category.getId());
        c.setName(category.getName());
        return categoryRepository.save(c);
    }

    @Override
    public void activatedById(Long id) {
        Category c = categoryRepository.getReferenceById(id);
        c.setActivated(true);
        c.setDeleted(false);
        categoryRepository.save(c);
    }

    @Override
    public void deleteById(Long id) {
        Category c = categoryRepository.getReferenceById(id);
        c.setActivated(false);
        c.setDeleted(true);
        categoryRepository.save(c);
    }


}
