package org.crochet.service;

import com.google.cloud.storage.Blob;
import com.google.firebase.cloud.StorageClient;
import lombok.extern.slf4j.Slf4j;
import org.crochet.exception.CloudStorageException;
import org.crochet.service.abstraction.FirebaseService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Slf4j
public class FirebaseServiceImpl implements FirebaseService {

  private static final String BUCKET_NAME = "littlecrochet.appspot.com";
  private final StorageClient storageClient;

  public FirebaseServiceImpl(StorageClient storageClient) {
    this.storageClient = storageClient;
  }

  @Override
  public byte[] updateLoadImage(MultipartFile imageFile) {
    // Define the path and filename in Firebase Cloud Storage
    String fileName = "images/" + imageFile.getOriginalFilename();

    // Upload the image to Firebase Cloud Storage
    Blob blob;
    try {
      blob = storageClient.bucket(BUCKET_NAME)
          .create(fileName, imageFile.getInputStream(), imageFile.getContentType());
    } catch (IOException e) {
      log.error("Cannot upload image to Firebase Cloud Storage");
      throw new CloudStorageException("Cannot upload image to Firebase Cloud Storage");
    }

    log.info("image name: {}", blob.getName());

    return blob.getContent();
  }

  @Override
  public byte[] getImage(String filename) {
    // Define the path and filename in Firebase Cloud Storage
    String filePath = filename;
    if (!filename.startsWith("images/")) {
      filePath = "images/" + filename;
    }

    Blob blob = storageClient.bucket(BUCKET_NAME).get(filePath);
    if (blob != null) {
      return blob.getContent();
    } else {
      log.error("Image not found in Firebase Cloud Storage");
      throw new CloudStorageException("Image not found in Firebase Cloud Storage");
    }
  }
}
