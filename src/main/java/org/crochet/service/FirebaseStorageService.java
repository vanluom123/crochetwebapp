package org.crochet.service;

import org.crochet.payload.response.FileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FirebaseStorageService {
    List<FileResponse> uploadMultipleFiles(MultipartFile[] files);

    List<String> deleteMultipleFiles(List<String> fileNames);

    FileResponse tryUploadFile(MultipartFile newFile);

    List<String> getAllFilesInStorage();

    List<String> getAllFilesInStorage(String folderName);
}
