package com.catcosy.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    private Category category;

//    @ManyToMany(fetch = FetchType.EAGER)
//    @JoinTable(name = "products_sizes",
//            joinColumns = @JoinColumn(name = "product_id", referencedColumnName = "product_id"),
//            inverseJoinColumns = @JoinColumn(name = "size_id", referencedColumnName = "size_id"))

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductSize> sizes;

    private Double costPrice;
    private Double salePrice;

    @Column(name = "total_quantity")
    private Integer quantity = 0;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductImage> images;

    private Boolean isDeleted;
    private Boolean isActivated;

}
