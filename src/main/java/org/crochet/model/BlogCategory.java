package org.crochet.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "blog_category")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BlogCategory extends BaseEntity {
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "blogCategory")
    @JsonManagedReference
    private Set<BlogPost> blogPosts;
}
