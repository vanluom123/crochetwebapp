package org.crochet.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import lombok.extern.slf4j.Slf4j;
import org.crochet.service.contact.BlogStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
public class BlogStorageServiceImpl implements BlogStorageService {
    private final BlobContainerClient blobContainerClient;

    public BlogStorageServiceImpl(BlobContainerClient blobContainerClient) {
        this.blobContainerClient = blobContainerClient;
    }

    @Override
    public List<String> uploadMultipleFile(List<MultipartFile> files) {
        return files.parallelStream()
                .map(file -> {
                    String blobName = file.getOriginalFilename();
                    BlobClient blobClient = blobContainerClient.getBlobClient(blobName);
                    try {
                        blobClient.upload(file.getInputStream(), file.getSize());
                        return blobClient.getBlobUrl();
                    } catch (Exception e) {
                        log.error("Error occurred while uploading file to blob storage", e);
                        throw new RuntimeException(e);
                    }
                }).toList();
    }
}
