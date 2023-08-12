package org.crochet.service.abstraction;

import org.crochet.request.ItemRequest;
import org.crochet.response.ItemResponse;

public interface ItemService {
  ItemResponse createOrUpdate(ItemRequest request);
}
