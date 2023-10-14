package org.crochet.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponse {
  private long id;
  private LocalDateTime orderDate;
  private double totalPrice;
}
