package org.crochet.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "category")
@SuperBuilder
@NoArgsConstructor
public class Category extends BaseEntity {
    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Category> children;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private Set<Product> products;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private Set<Pattern> patterns;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private Set<FreePattern> freePatterns;
}
