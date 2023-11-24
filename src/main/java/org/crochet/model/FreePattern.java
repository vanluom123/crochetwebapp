package org.crochet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Entity
@Table(name = "freePattern")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class FreePattern {
  @Id
  @GeneratedValue(strategy = GenerationType.TABLE)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "name")
  private String name;

  @Lob
  @Column(name = "image", columnDefinition = "LONGBLOB")
  private String image;

  @Column(name = "description")
  private String description;
}
