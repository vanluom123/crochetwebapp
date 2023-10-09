package org.crochet.controller;

import org.crochet.service.abstraction.FirebaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/image")
public class ImageController {

  private final FirebaseService firebaseService;

  @Autowired
  public ImageController(FirebaseService firebaseService) {
    this.firebaseService = firebaseService;
  }

  @PostMapping("/upload")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<String> uploadImage(@RequestPart("imageFile") MultipartFile imageFile) {
    var fileName = firebaseService.updateLoadImage(imageFile);
    return ResponseEntity.ok(fileName);
  }

  @GetMapping("/get-image")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> getImage(@RequestParam("fileName") String fileName) {
    var image = firebaseService.getImage(fileName);
    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.valueOf(MediaType.IMAGE_PNG_VALUE))
        .body(image);
  }
}
