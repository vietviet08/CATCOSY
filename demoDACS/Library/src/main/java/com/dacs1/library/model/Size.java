package com.dacs1.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sizes", uniqueConstraints = @UniqueConstraint(columnNames = "size"))
public class Size {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String size;
    @ManyToMany(mappedBy = "sizes")
    private Collection<Product> products;
}
