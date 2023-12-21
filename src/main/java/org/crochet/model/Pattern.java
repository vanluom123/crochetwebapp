package org.crochet.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.crochet.enumerator.CurrencyCode;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.JoinColumn;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "pattern")
@SuperBuilder
@NoArgsConstructor
public class Pattern extends BaseEntity {
    @Column(name = "name")
    private String name;

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

    @ElementCollection
    @CollectionTable(name = "pattern_file",
            joinColumns = {@JoinColumn(name = "pattern_id", nullable = false)})
    @Column(name = "file_name", columnDefinition = "LONGBLOB")
    private List<String> files;

    @OneToMany(mappedBy = "pattern", cascade = CascadeType.ALL)
    private List<OrderPatternDetail> orderPatternDetails;
}
