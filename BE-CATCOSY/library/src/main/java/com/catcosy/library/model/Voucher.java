package com.catcosy.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vouchers")
public class Voucher extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "voucher_id")
    private Long id;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "vouchers_customers",
            joinColumns = @JoinColumn(name = "voucher_id", referencedColumnName = "voucher_id"),
            inverseJoinColumns = @JoinColumn(name = "customer_id", referencedColumnName = "customer_id"))
    private List<Customer> customerUsedVoucher;

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
