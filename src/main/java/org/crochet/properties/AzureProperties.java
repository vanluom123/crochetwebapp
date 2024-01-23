package org.crochet.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "azure.storage")
public class AzureProperties {
    private String azureStorageConnectionString;
    private String azureStorageContainerName;
}
