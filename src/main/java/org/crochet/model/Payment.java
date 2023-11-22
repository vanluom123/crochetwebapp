package org.crochet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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

  @Column(name = "payment_date", nullable = false)
  private LocalDateTime paymentDate;

  @Column(name = "payment_amount", nullable = false)
  private Double paymentAmount;

  @Column(name = "payment_method")
  private String paymentMethod;

  @Column(name = "transaction_id", nullable = false)
  private String transactionId;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private StatusType status;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id")
  private Order order;
}
