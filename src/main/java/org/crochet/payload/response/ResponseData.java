package org.crochet.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ResponseData<T> {
    private String message;
    private int status;
    private T data;
}
