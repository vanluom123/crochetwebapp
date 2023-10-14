package org.crochet.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderRequest {
  private long id;
  private LocalDateTime orderDate;
  private double totalPrice;
}
