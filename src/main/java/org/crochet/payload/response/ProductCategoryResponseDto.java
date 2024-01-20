package org.crochet.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategoryResponseDto {
    private UUID id;
    private String categoryName;
    private ProductCategoryResponseDto parent;
}