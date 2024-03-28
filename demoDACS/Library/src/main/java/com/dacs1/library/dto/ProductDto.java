package com.dacs1.library.dto;

import com.dacs1.library.model.Category;
import com.dacs1.library.model.ProductImage;
import com.dacs1.library.model.ProductSize;
import com.dacs1.library.model.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

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
    private List<ProductImage> images;
    private String description;
    private Integer quantity;
    private Boolean deleted;
    private Boolean activated;
}
