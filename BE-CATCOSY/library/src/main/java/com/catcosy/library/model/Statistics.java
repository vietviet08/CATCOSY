package com.catcosy.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "statistics")
public class Statistics {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;
    private int year;
    private int month;
    private int day;
    private int totalOrder;
    private int totalProduct;
    private Double totalPrice;
    private int totalCustomer;

}
