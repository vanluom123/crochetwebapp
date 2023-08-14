package org.crochet.response;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ItemPaginationResponse {
  private List<ItemResponse> contents;
  private int pageNo;
  private int pageSize;
  private long totalElements;
  private int totalPages;
  private boolean last;
}
