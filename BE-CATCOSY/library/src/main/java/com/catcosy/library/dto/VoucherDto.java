package com.catcosy.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoucherDto {

    private Long id;

    private String codeVoucher;

    private Double price;

    private int percentOfTotalPrice;

    private Double minimumPrice;

    private int minimumTotalProduct;

    private int usageLimits;

    private Date expiryDate;

    private String forEmailCustomer;


}
