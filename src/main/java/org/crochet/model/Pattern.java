package org.crochet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "pattern")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Pattern extends ParentPattern {

    @Column(name = "price")
    private double price;

    @OneToMany(mappedBy = "pattern")
    private Set<OrderDetail> orderDetails;

    @OneToMany(mappedBy = "pattern")
    private Set<PatternImage> patternImages;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Override
    public Long getId() {
        return super.getId();
    }

    @Column(name = "name")
    @Override
    public String getName() {
        return super.getName();
    }

    @Column(name = "description")
    @Override
    public String getDescription() {
        return super.getDescription();
    }
}
