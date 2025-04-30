package com.catcosy.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products_sizes")
public class ProductSize extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "size_id", referencedColumnName = "size_id")
    private Size size;

    private Integer quantity;

    @PostUpdate
    public void updateProductTotal() {
        if (product != null) {
            int totalQuantity = product.getSizes().stream().mapToInt(ProductSize::getQuantity).sum();
            product.setQuantity(totalQuantity);
        }
    }

}
