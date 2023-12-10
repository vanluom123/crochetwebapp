package org.crochet.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class HomeResponse {
    private List<ProductResponse> products;
    private List<PatternResponse> patterns;
    private List<FreePatternResponse> freePatterns;
}
