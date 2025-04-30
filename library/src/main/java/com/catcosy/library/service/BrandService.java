package com.catcosy.library.service;

import com.catcosy.library.model.Brand;
import com.catcosy.library.model.Category;

import java.util.List;
import java.util.Optional;


public interface BrandService {

    List<Long> getAllIdBrand();

    List<Brand> findAllBrand();

    List<Brand> findAllBrandIsActivate();

    Optional<Brand> findById(Long id);

    Brand getById(Long id);

    Brand save(Brand brand);

    Brand update(Brand brand);

    void activatedById(Long id);

    void deleteById(Long id);

}
