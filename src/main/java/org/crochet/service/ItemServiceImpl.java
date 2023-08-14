package org.crochet.service;

import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.ItemMapper;
import org.crochet.model.Item;
import org.crochet.repository.ItemRepository;
import org.crochet.repository.ItemSpecifications;
import org.crochet.request.ItemRequest;
import org.crochet.response.ItemPaginationResponse;
import org.crochet.response.ItemResponse;
import org.crochet.service.abstraction.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
          .setName(request.getName())
          .setPrice(request.getPrice())
          .setDescription(request.getDescription())
          .setImage(request.getImage());
    } else {
      // update item
      item.setName(request.getName())
          .setPrice(request.getPrice())
          .setDescription(request.getDescription())
          .setImage(request.getImage());
    }
    item = itemRepository.save(item);
    return ItemMapper.INSTANCE.toResponse(item);
  }

  @Override
  public ItemPaginationResponse getPaginatedMenuItem(int pageNo, int pageSize, String sortBy, String sortDir, String researchText) {
    // create Sort instance
    Sort sort = Sort.by(sortBy);
    sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? sort.ascending() : sort.descending();
    // create Pageable instance
    Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
    Page<Item> menuPage;

    if (researchText == null) {
      menuPage = itemRepository.findAll(pageable);
    } else {
      menuPage = itemRepository.findAll(ItemSpecifications.researchItems(researchText), pageable);
    }

    List<ItemResponse> contents = ItemMapper.INSTANCE.toResponses(menuPage.getContent());

    return new ItemPaginationResponse()
        .setContents(contents)
        .setPageNo(menuPage.getNumber())
        .setPageSize(menuPage.getSize())
        .setTotalElements(menuPage.getTotalElements())
        .setTotalPages(menuPage.getTotalPages())
        .setLast(menuPage.isLast());
  }

  @Override
  public ItemResponse getDetail(long id) {
    var item = itemRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
    return ItemMapper.INSTANCE.toResponse(item);
  }
}
