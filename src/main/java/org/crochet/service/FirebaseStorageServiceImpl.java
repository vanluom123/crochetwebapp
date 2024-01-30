package org.crochet.service;

import com.google.cloud.storage.Blob;
import com.google.firebase.cloud.StorageClient;
import lombok.extern.slf4j.Slf4j;
import org.crochet.service.contact.FirebaseStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
public class FirebaseStorageServiceImpl implements FirebaseStorageService {
    private static final String BUCKET_NAME = "littlecrochet.appspot.com";
    private final StorageClient storageClient;

    public FirebaseStorageServiceImpl(StorageClient storageClient) {
        this.storageClient = storageClient;
    }

    public String uploadFile(MultipartFile imageFile) {
        // Define the path and filename in Firebase Cloud Storage
        String fileName = "images/" + imageFile.getOriginalFilename();

        // Upload the image to Firebase Cloud Storage
        Blob blob;
        try {
            blob = storageClient.bucket(BUCKET_NAME)
                    .create(fileName, imageFile.getInputStream(), imageFile.getContentType());
        } catch (IOException e) {
            log.error("Cannot upload image to Firebase Cloud Storage");
            throw new RuntimeException("Cannot upload image to Firebase Cloud Storage");
        }

        log.info("image name: {}", blob.getName());

        String blobName = blob.getName().replaceFirst("images/", "");

        return "https://firebasestorage.googleapis.com/v0/b/" + BUCKET_NAME + "/o/images%2F" + blobName + "?alt=media";
    }

    @Override
    public List<String> uploadMultipleFiles(MultipartFile[] files) {
        List<String> fileNames;
        fileNames = Stream.of(files)
                .parallel()
                .map(this::uploadFile)
                .toList();
        return fileNames;
    }
}
