package org.crochet.request;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ItemRequest {
  private long id;
  private String name;
  private String image;
  private String description;
  private double price;
}
