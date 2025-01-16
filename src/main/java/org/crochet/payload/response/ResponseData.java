package org.crochet.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ResponseData<T> {
    private boolean success;
    private int code;
    private String message;
    private T data;
    private Throwable error;
}
