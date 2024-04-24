package org.crochet.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BannerRequest {
    private UUID id;
    @NotNull(message = "Banner type id is required")
    private UUID bannerTypeId;
    private String title;
    private String content;
    private String url;
    private String fileName;
    private String fileContent;
    private boolean active;
}