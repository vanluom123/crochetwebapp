package org.crochet.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "category_free_pattern")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryFreePattern extends BaseEntity {
    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private CategoryFreePattern parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<CategoryFreePattern> children;

    @OneToMany(mappedBy = "categoryFreePattern", cascade = CascadeType.ALL)
    private Set<FreePattern> freePatterns;
}
