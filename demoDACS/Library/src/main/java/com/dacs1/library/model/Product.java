package com.dacs1.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GeneratedColumn;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;
    private String name;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    private Category category;
    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<Size> sizes;
    private Double costPrice;
    private Double salePrice;
    private Integer quantity;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private String img1;
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private String img2;
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private String img3;
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private String img4;
    private Boolean isDeleted;
    private Boolean isActivated;
}
