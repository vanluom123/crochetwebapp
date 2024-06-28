package org.crochet.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FreeChartDetailResponse {
    private String id;
    private String name;
    private String description;
    private String author;
    private boolean isHome;
    private String link;
    private String content;
    private List<FileResponse> images;
    private List<FileResponse> files;
    private CategoryResponse category;
}
