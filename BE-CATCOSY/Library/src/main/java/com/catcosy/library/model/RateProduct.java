package com.catcosy.library.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
public class RateProduct extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rate_id")
    private Long id;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id")
    private Customer customer;

    private int star;

    @Column(columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "rateProduct", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RateProductImage> images;

    private LocalDateTime dateUpload;

    private boolean rated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    private Product product;

    private String sizeAndQuantity;

    private int amountOfLike;

    private boolean isDelete;

    @OneToMany(mappedBy = "rateProduct", cascade = CascadeType.ALL)
    private List<CustomerLikedComment> customersLikedComment;


}
