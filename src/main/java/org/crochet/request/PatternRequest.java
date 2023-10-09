package org.crochet.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PatternRequest {
  private long id;
  private String name;
  private String image;
  private String description;
  private double price;
}
