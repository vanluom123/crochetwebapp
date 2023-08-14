package org.crochet.service.abstraction;

import org.springframework.web.multipart.MultipartFile;

public interface FirebaseService {
  String updateLoadImage(MultipartFile imageFile);
  byte[] getImage(String filename);

}
