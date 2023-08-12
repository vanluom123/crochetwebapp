package org.crochet.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemResponse {
  private long id;
  private byte[] image;
  private String description;
  private double price;
}
