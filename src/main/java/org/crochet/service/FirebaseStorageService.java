package org.crochet.service;

import org.crochet.payload.response.FileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface FirebaseStorageService {
    List<FileResponse> uploadMultipleFiles(MultipartFile[] files);

    List<String> deleteMultipleFiles(List<String> fileNames);

    FileResponse updateFile(MultipartFile newFile, String existingFileName);
}
