package org.crochet.service.contact;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FirebaseService {
    List<String> uploadFiles(MultipartFile[] files);

    String uploadFile(MultipartFile imageFile);

    byte[] getFile(String filename);

}
