package com.dacs1.library.dto;

import com.dacs1.library.model.Category;
import com.dacs1.library.model.Size;

import java.util.List;

public class ProductDto {
    private Long id;
    private String name;
    private Category category;
    private List<Size> sizes;
    private Double costPrice;
    private Double salePrice;
    private String img1;
    private String img2;
    private String img3;
    private String img4;
    private String description;
    private Integer quantity;
    private Boolean deleted;
    private Boolean activated;
}
