package com.catcosy.library.service;

import com.catcosy.library.model.Size;

import java.util.List;

public interface SizeService {

    List<Size> findAllSize();

    List<Size> findAllSizeById();

    List<Long> getAllSizeId();

    Size getSizeById(Long id);

}
