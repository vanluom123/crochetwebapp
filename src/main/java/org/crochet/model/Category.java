package org.crochet.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.BatchSize;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "category")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Category extends BaseEntity {
    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    @JsonBackReference
    private Category parent;

    @BatchSize(size = 10)
    @OneToMany(mappedBy = "parent",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonManagedReference
    private List<Category> children;

    @OneToMany(mappedBy = "category",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Product> products;

    @OneToMany(mappedBy = "category",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Pattern> patterns;

    @OneToMany(mappedBy = "category",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<FreePattern> freePatterns;
}
