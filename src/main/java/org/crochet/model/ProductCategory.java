package org.crochet.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "product_category")
@SuperBuilder
@NoArgsConstructor
public class ProductCategory extends BaseEntity {
    @Column(name = "category_name", unique = true, nullable = false)
    private String categoryName;

    @OneToMany(mappedBy = "productCategory", cascade = CascadeType.ALL)
    private Set<Product> products;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_category_id", columnDefinition = "BINARY(16) NULL")
    private ProductCategory parentCategory;
}
