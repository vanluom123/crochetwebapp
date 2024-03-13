package org.crochet.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FirebaseStorageService {
    List<String> uploadMultipleFiles(MultipartFile[] files);
}
