package org.crochet.service.impl;

import com.google.cloud.storage.Blob;
import com.google.firebase.cloud.StorageClient;
import lombok.extern.slf4j.Slf4j;
import org.crochet.properties.MessageCodeProperties;
import org.crochet.exception.StorageException;
import org.crochet.payload.response.FileResponse;
import org.crochet.service.FirebaseStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class FirebaseStorageServiceImpl implements FirebaseStorageService {
    private static final String BUCKET_NAME = "littlecrochet.appspot.com";
    public static final String CANNOT_UPLOAD_FILE_MESSAGE = "Cannot upload image to Firebase Cloud Storage";
    private final StorageClient storageClient;
    private final MessageCodeProperties msgCodeProps;

    public FirebaseStorageServiceImpl(StorageClient storageClient, MessageCodeProperties msgCodeProps) {
        this.storageClient = storageClient;
        this.msgCodeProps = msgCodeProps;
    }

    public FileResponse uploadFile(MultipartFile imageFile) {
        // Define the path and filename in Firebase Cloud Storage
        String fileName = "images/" + imageFile.getOriginalFilename();

        // Upload the image to Firebase Cloud Storage
        Blob blob;
        try {
            blob = storageClient.bucket(BUCKET_NAME)
                    .create(fileName, imageFile.getInputStream(), imageFile.getContentType());
        } catch (IOException e) {
            log.error(CANNOT_UPLOAD_FILE_MESSAGE);
            throw new StorageException(CANNOT_UPLOAD_FILE_MESSAGE,
                    msgCodeProps.getCode("CANNOT_UPLOAD_FILE_MESSAGE"));
        }

        log.info("image name: {}", blob.getName());

        String blobName = blob.getName().replaceFirst("images/", "");
        String fileContent = "https://firebasestorage.googleapis.com/v0/b/" + BUCKET_NAME + "/o/images%2F" + blobName + "?alt=media";

        return new FileResponse(fileName, fileContent);
    }

    @Override
    public List<FileResponse> uploadMultipleFiles(MultipartFile[] files) {
        List<FileResponse> fileNames;
        fileNames = Stream.of(files)
                .map(this::uploadFile)
                .toList();
        return fileNames;
    }

    @Override
    public List<String> deleteMultipleFiles(List<String> fileNames) {
        return fileNames.stream()
                .map(this::tryDeleteFile)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

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

    @Override
    public FileResponse updateFile(MultipartFile newFile, String existingFileName) {
        // Delete the existing file
        Blob blob = storageClient.bucket(BUCKET_NAME).get(existingFileName);
        if (blob != null) {
            blob.delete();
            log.info("File {} has been deleted", existingFileName);
        } else {
            log.error("File {} does not exist", existingFileName);
            throw new StorageException("File does not exist",
                    msgCodeProps.getCode("FILE_NOT_FOUND_MESSAGE"));
        }

        // Upload the new file
        return uploadFile(newFile);
    }
}
