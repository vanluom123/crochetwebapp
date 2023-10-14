package org.crochet.service.abstraction;

import org.springframework.web.multipart.MultipartFile;

public interface FirebaseService {
  byte[] updateLoadImage(MultipartFile imageFile);
  byte[] getImage(String filename);

}
