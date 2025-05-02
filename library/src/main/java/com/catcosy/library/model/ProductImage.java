package com.catcosy.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products_images")
public class ProductImage extends BaseEntity {

    @Id
    @Column(name = "id_product_image")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    private Product product;

    @Lob
    @Column(columnDefinition = "LONGBLOB", name = "image")
    private String image;
    
    @Column(name = "s3_url", nullable = false)
    private String s3Url;
    
    @Column(name = "using_s3")
    private Boolean usingS3 = false;
    
    @Transient
    public String getImagePath() {
        return s3Url;
    }
}
