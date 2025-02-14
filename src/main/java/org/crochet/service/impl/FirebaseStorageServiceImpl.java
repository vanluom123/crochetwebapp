package org.crochet.service.impl;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.firebase.cloud.StorageClient;
import lombok.extern.slf4j.Slf4j;
import org.crochet.enums.ResultCode;
import org.crochet.exception.StorageException;
import org.crochet.payload.response.FileResponse;
import org.crochet.service.FirebaseStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class FirebaseStorageServiceImpl implements FirebaseStorageService {
    private static final String BUCKET_NAME = "littlecrochet.appspot.com";
    private final StorageClient storageClient;
    private final String env;

    /**
     * Constructor
     *
     * @param storageClient The Firebase Cloud Storage client
     */
    public FirebaseStorageServiceImpl(StorageClient storageClient) {
        this.storageClient = storageClient;
        env = System.getenv("env");
    }

    /**
     * Upload a file to Firebase Cloud Storage
     *
     * @param imageFile The file to be uploaded
     * @return The file name and download URL
     */
    public FileResponse uploadFile(MultipartFile imageFile) {
        // Define the path and filename in Firebase Cloud Storage
        String fileName = UUID.randomUUID().toString().concat(this.getExtension(Objects.requireNonNull(imageFile.getOriginalFilename())));
        fileName = env + "/" + fileName;

        // Upload the image to Firebase Cloud Storage
        Blob blob;
        try {
            blob = storageClient.bucket(BUCKET_NAME)
                    .create(fileName, imageFile.getInputStream(), imageFile.getContentType());
        } catch (IOException e) {
            log.error(ResultCode.ERROR_IMAGE_UPLOAD_FAILED.message());
            throw new StorageException(ResultCode.ERROR_IMAGE_UPLOAD_FAILED.message(),
                    ResultCode.ERROR_IMAGE_UPLOAD_FAILED.code());
        }

        log.info("image name: {}", blob.getName());

        var lastModified = blob.getUpdateTimeOffsetDateTime().toLocalDateTime();

        var encodeName = URLEncoder.encode(blob.getName(), StandardCharsets.UTF_8);

        String downloadUrl = "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media";
        var fileContent = String.format(downloadUrl, BUCKET_NAME, encodeName);

        return new FileResponse(fileName, fileContent, lastModified);
    }

    /**
     * Upload multiple files to Firebase Cloud Storage
     *
     * @param files The list of files to be uploaded
     * @return The list of file names
     */
    @Override
    public List<FileResponse> uploadMultipleFiles(MultipartFile[] files) {
        return IntStream.range(0, files.length)
                .mapToObj(i -> {
                    FileResponse response = tryUploadFile(files[i]);
                    response.setOrder(i);
                    return response;
                })
                .collect(Collectors.toList());
    }

    /**
     * Delete a file from Firebase Cloud Storage
     *
     * @param fileNames The name of the file to be deleted
     * @return The name of the deleted file
     */
    @Override
    public List<String> deleteMultipleFiles(List<String> fileNames) {
        return fileNames.stream()
                .map(this::tryDeleteFile)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    /**
     * Try to delete a file from Firebase Cloud Storage
     *
     * @param fileName The name of the file to be deleted
     * @return The name of the deleted file
     */
    private Optional<String> tryDeleteFile(String fileName) {
        Blob blob = storageClient.bucket(BUCKET_NAME).get(fileName);
        if (blob != null) {
            blob.delete();
            log.info("File {} has been deleted", fileName);
            return Optional.empty();
        } else {
            log.error("File {} does not exist", fileName);
            return Optional.of(fileName);
        }
    }

    /**
     * Try to upload a file to Firebase Cloud Storage
     *
     * @param newFile The file to be uploaded
     * @return The file name and download URL
     */
    @Override
    public FileResponse tryUploadFile(MultipartFile newFile) {
        String fileName = env + "/" + newFile.getOriginalFilename();
        Blob blob = storageClient.bucket(BUCKET_NAME).get(fileName);
        if (blob != null) {
            // Delete the existing file
            blob.delete();
            log.info("Deleted {}", fileName);
        }

        // Upload the new file
        return uploadFile(newFile);
    }

    /**
     * Get all files in Firebase Cloud Storage
     *
     * @return The list of file names
     */
    @Override
    public List<String> getAllFilesInStorage() {
        Bucket bucket = storageClient.bucket(BUCKET_NAME);
        return StreamSupport.stream(bucket.list().iterateAll().spliterator(), false)
                .map(Blob::getName)
                .toList();
    }

    /**
     * Get all files in a specific folder in Firebase Cloud Storage
     *
     * @param folderName The name of the folder
     * @return The list of file names
     */
    @Override
    public List<String> getAllFilesInStorage(String folderName) {
        Bucket bucket = storageClient.bucket(BUCKET_NAME);
        return StreamSupport.stream(bucket.list(Storage.BlobListOption.prefix(folderName + "/")).iterateAll().spliterator(), false)
                .map(Blob::getName)
                .toList();
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
