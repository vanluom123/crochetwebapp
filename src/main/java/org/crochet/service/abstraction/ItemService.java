package org.crochet.service.abstraction;

import org.crochet.request.ItemRequest;
import org.crochet.response.ItemPaginationResponse;
import org.crochet.response.ItemResponse;

public interface ItemService {
  ItemResponse createOrUpdate(ItemRequest request);

  ItemPaginationResponse getPaginatedMenuItem(int pageNo, int pageSize, String sortBy, String sortDir, String researchText);

  ItemResponse getDetail(long id);
}