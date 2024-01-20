package org.crochet.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiError {
    private String message;
    private int code;
    private String error;
}
