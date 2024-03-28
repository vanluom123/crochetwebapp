package org.crochet.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.crochet.enumerator.CurrencyCode;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "pattern")
@SuperBuilder
@NoArgsConstructor
public class Pattern extends BaseEntity {
    @Column(name = "name")
    private String name;

    @Lob
    @Column(name = "description", columnDefinition = "LONGBLOB")
    private String description;

    @Column(name = "price",
            columnDefinition = "double default 0",
            nullable = false)
    private double price;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency_code",
            columnDefinition = "varchar(20) default 'USD'",
            nullable = false)
    private CurrencyCode currencyCode;

    @OneToMany(mappedBy = "pattern", cascade = CascadeType.ALL)
    private Set<OrderPatternDetail> orderPatternDetails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", columnDefinition = "BINARY(16) NOT NULL")
    private Category category;

    @OneToMany(mappedBy = "pattern", cascade = CascadeType.ALL)
    private Set<File> files;

    @OneToMany(mappedBy = "pattern", cascade = CascadeType.ALL)
    private Set<Image> images;
}
