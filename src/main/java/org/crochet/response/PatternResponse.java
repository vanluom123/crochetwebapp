package org.crochet.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PatternResponse {
  private Long id;
  private String name;
  private String image;
  private String description;
  private double price;
}
