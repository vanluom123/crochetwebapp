package org.crochet.service;

import org.springframework.web.multipart.MultipartFile;

public interface FirebaseService {
    byte[] updateLoadImage(MultipartFile imageFile);

    byte[] getImage(String filename);

}
