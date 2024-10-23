package com.catcosy.library.service.impl;

import com.catcosy.library.repository.SizeRepository;
import com.catcosy.library.model.Size;
import com.catcosy.library.service.SizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SizeServiceImpl implements SizeService {

    @Autowired
    private SizeRepository sizeRepository;

    @Override
    public List<Size> findAllSize() {
        return sizeRepository.findAll();
    }

    @Override
    public List<Size> findAllSizeById() {
        return null;
    }

    @Override
    public List<Long> getAllSizeId() {
        List<Long> sizeIds = new ArrayList<>();
        List<Size> sizes =   sizeRepository.findAll();
        for(Size s : sizes){
            sizeIds.add(s.getId());
        }
        return sizeIds;
    }

    @Override
    public Size getSizeById(Long id) {
        return sizeRepository.getReferenceById(id);
    }
}
