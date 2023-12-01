package org.crochet.service;

import com.google.cloud.storage.Blob;
import com.google.firebase.cloud.StorageClient;
import lombok.extern.slf4j.Slf4j;
import org.crochet.exception.CloudStorageException;
import org.crochet.service.contact.FirebaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * FirebaseServiceImpl class
 */
@Service
@Slf4j
public class FirebaseServiceImpl implements FirebaseService {

    private static final String BUCKET_NAME = "littlecrochet.appspot.com";

    private final StorageClient storageClient;

    /**
     * Constructs a new {@code FirebaseServiceImpl} with the specified StorageClient.
     *
     * @param storageClient The StorageClient for handling Firebase storage operations.
     */
    public FirebaseServiceImpl(StorageClient storageClient) {
        this.storageClient = storageClient;
    }

    /**
     * Uploads an array of {@link MultipartFile} objects and returns a list of corresponding file URLs or identifiers.
     *
     * @param files An array of {@link MultipartFile} objects representing the files to be uploaded.
     * @return A list of strings representing the URLs or identifiers of the uploaded files.
     */
    @Override
    public List<String> uploadFiles(MultipartFile[] files) {
        return Arrays.stream(files)
                .map(this::uploadFile)
                .toList();
    }

    /**
     * Uploads a single {@link MultipartFile} image file to Firebase Cloud Storage.
     *
     * @param imageFile The {@link MultipartFile} representing the image file to be uploaded.
     * @return A string representing the path and filename in Firebase Cloud Storage.
     * @throws CloudStorageException If there is an error during the upload process.
     */
    @Override
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
            throw new CloudStorageException("Cannot upload image to Firebase Cloud Storage");
        }

        log.info("image name: {}", blob.getName());

        return fileName;
    }

    /**
     * Retrieves the content of a file from Firebase Cloud Storage based on the specified filename.
     *
     * @param filename The path and filename of the file in Firebase Cloud Storage.
     * @return A byte array representing the content of the file.
     * @throws CloudStorageException If the file is not found or there is an error retrieving the file content.
     */
    @Override
    public byte[] getFile(String filename) {
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
