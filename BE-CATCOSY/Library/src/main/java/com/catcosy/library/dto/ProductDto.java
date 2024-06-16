package com.catcosy.library.dto;

import com.catcosy.library.model.Category;
import com.catcosy.library.model.ProductSize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long id;
    private String name;
    private Category category;
    private List<ProductSize> sizes;
    private Double costPrice;
    private Double salePrice;
    private List<String> images;
    private String description;
    private Integer quantity;
    private Boolean deleted;
    private Boolean activated;
}
