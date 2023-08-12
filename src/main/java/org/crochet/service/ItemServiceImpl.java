package org.crochet.service;

import org.crochet.mapper.ItemMapper;
import org.crochet.model.Item;
import org.crochet.repository.ItemRepository;
import org.crochet.request.ItemRequest;
import org.crochet.response.ItemResponse;
import org.crochet.service.abstraction.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItemServiceImpl implements ItemService {

  private final ItemRepository itemRepository;

  @Autowired
  public ItemServiceImpl(ItemRepository itemRepository) {
    this.itemRepository = itemRepository;
  }

  @Transactional
  @Override
  public ItemResponse createOrUpdate(ItemRequest request) {
    var item = itemRepository.findById(request.getId()).orElse(null);
    if (item == null) {
      // create item
      item = new Item()
          .setId(request.getId())
          .setPrice(request.getPrice())
          .setDescription(request.getDescription())
          .setImage(request.getImage());
    } else {
      // update item
      item.setPrice(request.getPrice())
          .setDescription(request.getDescription())
          .setImage(request.getImage());
    }
    item = itemRepository.save(item);
    return ItemMapper.INSTANCE.toResponse(item);
  }
}
