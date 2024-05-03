package com.dacs1.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vouchers")
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codeVoucher;

    private Double price;

    private int percentOfTotalPrice;

    private Double minimumPrice;

    private int minimumTotalProduct;

    private int usageLimits;

    private Date expiryDate;

    private String forEmailCustomer;

    private boolean isUsed = false;

    private boolean isActivated = true;

}
