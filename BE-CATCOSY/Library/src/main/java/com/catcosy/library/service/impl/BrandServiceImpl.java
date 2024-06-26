package com.catcosy.library.service.impl;

import com.catcosy.library.model.Brand;
import com.catcosy.library.model.Category;
import com.catcosy.library.repository.BrandRepository;
import com.catcosy.library.repository.CategoryRepository;
import com.catcosy.library.service.BrandService;
import com.catcosy.library.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BrandServiceImpl implements BrandService {


    @Autowired
    private BrandRepository brandRepository;

    @Override
    public List<Brand> findAllBrand() {
        return brandRepository.findAll();
    }

    @Override
    public List<Brand> findAllBrandIsActivate(){
        return brandRepository.findAllByBrandIsActivate();
    }

    @Override
    public Optional<Brand> findById(Long id) {
        return brandRepository.findById(id);
    }

    @Override
    public Brand getById(Long id) {
        return brandRepository.getReferenceById(id);
    }

    @Override
    public Brand save(Brand brand){
        Brand c = new Brand();
        c.setName(brand.getName());
        c.setActivated(true);
        c.setDeleted(false);
        return brandRepository.save(c);
    }

    @Override
    public Brand update(Brand brand) {
        Brand c = brandRepository.getReferenceById(brand.getId());
        c.setName(brand.getName());
        return brandRepository.save(c);
    }

    @Override
    public void activatedById(Long id) {
        Brand c = brandRepository.getReferenceById(id);
        c.setActivated(true);
        c.setDeleted(false);
        brandRepository.save(c);
    }

    @Override
    public void deleteById(Long id) {
        Brand c = brandRepository.getReferenceById(id);
        c.setActivated(false);
        c.setDeleted(true);
        brandRepository.save(c);
    }


}
