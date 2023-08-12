package org.crochet.controller;

import org.crochet.constant.AppConstant;
import org.crochet.request.ItemRequest;
import org.crochet.response.ItemPaginationResponse;
import org.crochet.response.ItemResponse;
import org.crochet.service.abstraction.FirebaseService;
import org.crochet.service.abstraction.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@RestController
@RequestMapping("/item")
public class ItemController {
  private final ItemService itemService;
  private final FirebaseService firebaseService;

  @Autowired
  public ItemController(ItemService itemService, FirebaseService firebaseService) {
    this.itemService = itemService;
    this.firebaseService = firebaseService;
  }

  @PostMapping("/create")
  public ResponseEntity<String> createItem(@RequestParam("filePath") String filePath,
                                           @RequestBody ItemRequest request) {
    var byteData = firebaseService.getImage(filePath);
    var image = Base64.getEncoder().encodeToString(byteData);
    request.setImage(image);
    itemService.createOrUpdate(request);
    return ResponseEntity.ok("Create item successfully");
  }

  @GetMapping("/pagination")
  public ResponseEntity<ItemPaginationResponse> getPaginatedMenuItem(
      @RequestParam(value = "pageNo", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
      @RequestParam(value = "pageSize", defaultValue = AppConstant.DEFAULT_PAGE_SIZE, required = false) int pageSize,
      @RequestParam(value = "sortBy", defaultValue = AppConstant.DEFAULT_SORT_BY, required = false) String sortBy,
      @RequestParam(value = "sortDir", defaultValue = AppConstant.DEFAULT_SORT_DIRECTION,
          required = false) String sortDir,
      @RequestParam(value = "researchText", required = false) String researchText) {
    var response = itemService.getPaginatedMenuItem(pageNo, pageSize, sortBy, sortDir, researchText);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/detail")
  public ResponseEntity<ItemResponse> getDetail(@RequestParam("id") long id) {
    return ResponseEntity.ok(itemService.getDetail(id));
  }
}
