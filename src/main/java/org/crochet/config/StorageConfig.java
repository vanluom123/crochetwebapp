package org.crochet.config;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.crochet.properties.AzureProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfig {
    private final AzureProperties azureProps;

    public StorageConfig(AzureProperties azureProps) {
        this.azureProps = azureProps;
    }

    @Bean
    public BlobServiceClient blobServiceClient() {
        return new BlobServiceClientBuilder()
                .connectionString(azureProps.getAzureStorageConnectionString())
                .buildClient();
    }

    @Bean
    public BlobContainerClient blobContainerClient() {
        return blobServiceClient()
                .getBlobContainerClient(azureProps.getAzureStorageContainerName());
    }
}
