package org.crochet.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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

import java.util.Set;

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
    @OneToMany(mappedBy = "parent")
    @JsonManagedReference
    private Set<Category> children;

    @OneToMany(mappedBy = "category")
    private Set<Product> products;

    @OneToMany(mappedBy = "category")
    private Set<Pattern> patterns;

    @OneToMany(mappedBy = "category")
    private Set<FreePattern> freePatterns;
}
