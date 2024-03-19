package com.dacs1.library.service.impl;

import com.dacs1.library.model.Size;
import com.dacs1.library.repository.SizeRepository;
import com.dacs1.library.service.SizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SizeServiceImpl implements SizeService {

    @Autowired
    private SizeRepository sizeRepository;

    @Override
    public List<Size> findAllSize() {
        return sizeRepository.findAll();
    }
}
