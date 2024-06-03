package com.dacs1.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comment_images")
public class RateProductImage {

    @Id
    @Column(name = "id_comment_image")
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rate_id", referencedColumnName = "rate_id")
    private RateProduct rateProduct;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private String image;


}
