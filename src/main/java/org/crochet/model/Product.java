package org.crochet.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.crochet.enumerator.CurrencyCode;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "product")
@SuperBuilder
@NoArgsConstructor
public class Product extends BaseEntity {
    @Column(name = "name")
    private String name;

    @Lob
    @Column(name = "description", columnDefinition = "LONGBLOB")
    private String description;

    @Column(name = "price", columnDefinition = "double default 0", nullable = false)
    private double price;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency_code",
            columnDefinition = "varchar(20) default 'USD'",
            nullable = false)
    private CurrencyCode currencyCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", columnDefinition = "BINARY(16) NOT NULL")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<File> files;
}
