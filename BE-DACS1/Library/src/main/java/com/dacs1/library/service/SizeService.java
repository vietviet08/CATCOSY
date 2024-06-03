package com.dacs1.library.service;

import com.dacs1.library.model.Size;

import java.util.List;

public interface SizeService {

    List<Size> findAllSize();

    List<Size> findAllSizeById();

    List<Long> getAllSizeId();

    Size getSizeById(Long id);

}
