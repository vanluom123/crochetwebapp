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
@Table(name = "category_pattern")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryPattern extends BaseEntity {
    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private CategoryPattern parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<CategoryPattern> children;

    @OneToMany(mappedBy = "categoryPattern", cascade = CascadeType.ALL)
    private Set<Pattern> patterns;
}
