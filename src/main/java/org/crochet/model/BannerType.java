package org.crochet.model;

import jakarta.persistence.CascadeType;
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
@Table(name = "banner_type")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BannerType extends BaseEntity {
    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @OneToMany(mappedBy = "bannerType", cascade = CascadeType.ALL)
    private Set<Banner> banners;
}
