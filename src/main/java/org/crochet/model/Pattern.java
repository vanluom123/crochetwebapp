package org.crochet.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "pattern")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Pattern extends BasePattern {
    @Column(name = "price")
    private double price;

    @ElementCollection
    @CollectionTable(name = "pattern_file",
            joinColumns = {@JoinColumn(name = "pattern_id", nullable = false)})
    @Column(name = "file_name", columnDefinition = "LONGBLOB")
    private List<String> files;
}
