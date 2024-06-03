package com.dacs1.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDto {

    private Long id;
    private String image;
    private Long idProduct;
    private String nameProduct;
    private Double unitPrice;
    private String quantityAndSize;
    private Double totalPrice;
    private boolean isAllowComment;

}
