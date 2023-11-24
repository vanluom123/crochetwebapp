package org.crochet.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class PaginatedFreePatternResponse {
  private List<FreePatternResponse> contents;
  private int pageNo;
  private int pageSize;
  private long totalElements;
  private int totalPages;
  private boolean last;
}
