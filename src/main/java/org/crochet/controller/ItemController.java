package org.crochet.controller;

import org.crochet.request.ItemRequest;
import org.crochet.service.abstraction.FirebaseService;
import org.crochet.service.abstraction.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
}
