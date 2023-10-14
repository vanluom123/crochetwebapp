package org.crochet.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PatternRequest {
  private long id;
  private String name;
  private String image;
  private String description;
  private double price;
}
