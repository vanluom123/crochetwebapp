package org.crochet.payload.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavingChartRequest {
    @NotEmpty(message = "FreePatternId cannot be empty")
    private String freePatternId;

    @NotEmpty(message = "UserId cannot be empty")
    private String userId;
}
