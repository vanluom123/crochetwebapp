package org.crochet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

@Data
@Entity
@Table(name = "pattern")
@Accessors(chain = true)
public class Pattern {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "name")
  private String name;

  @Lob
  @Column(name = "image", columnDefinition = "LONGBLOB")
  private String image;

  @Column(name = "description")
  private String description;

  @Column(name = "price")
  private double price;

  @OneToMany(mappedBy = "pattern")
  private Set<OrderDetail> orderDetails;
}
