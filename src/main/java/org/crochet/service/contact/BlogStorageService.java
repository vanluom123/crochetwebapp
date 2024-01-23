package org.crochet.service.contact;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BlogStorageService {
    List<String> uploadMultipleFile(List<MultipartFile> file);
}
