package org.crochet.request;

import lombok.Data;

@Data
public class PatternRequest {
  private long id;
  private String name;
  private String image;
  private String description;
  private double price;
}
