package org.crochet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "payment")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  private LocalDateTime paymentDate;

  private Double paymentAmount;

  private String paymentMethod;

  private String transactionId;

  @NotNull
  @Enumerated(EnumType.STRING)
  private StatusType status;

  @ManyToOne
  @JoinColumn(name = "order_id")
  private Order order;
}
