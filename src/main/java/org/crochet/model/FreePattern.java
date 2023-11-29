package org.crochet.model;

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
@Table(name = "freePattern")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FreePattern extends BasePattern {
    @OneToMany(mappedBy = "freePattern")
    private Set<FreePatternFile> freePatternImages;
}
