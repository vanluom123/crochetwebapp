package org.crochet.service.impl;

import com.google.cloud.storage.Blob;
import com.google.firebase.cloud.StorageClient;
import lombok.extern.slf4j.Slf4j;
import org.crochet.constant.MessageConstant;
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

import static org.crochet.constant.MessageCodeConstant.MAP_CODE;

@Slf4j
@Service
public class FirebaseStorageServiceImpl implements FirebaseStorageService {
    private static final String BUCKET_NAME = "littlecrochet.appspot.com";
    private final StorageClient storageClient;

    public FirebaseStorageServiceImpl(StorageClient storageClient) {
        this.storageClient = storageClient;
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
            log.error(MessageConstant.CANNOT_UPLOAD_FILE_MESSAGE);
            throw new StorageException(MessageConstant.CANNOT_UPLOAD_FILE_MESSAGE, MAP_CODE.get(MessageConstant.CANNOT_UPLOAD_FILE_MESSAGE));
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
                .map(this::tryUploadFile)
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
    public FileResponse tryUploadFile(MultipartFile newFile) {
        String fileName = "images/" + newFile.getOriginalFilename();
        Blob blob = storageClient.bucket(BUCKET_NAME).get(fileName);
        if (blob != null) {
            // Delete the existing file
            blob.delete();
            log.info("Deleted {}", fileName);
        }

        // Upload the new file
        return uploadFile(newFile);
    }
}
