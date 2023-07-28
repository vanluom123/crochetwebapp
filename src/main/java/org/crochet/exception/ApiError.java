package org.crochet.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiError {
  private String message;
  private HttpStatus statusCode;
  private Throwable rootCause;
  public static ApiError create() {
    return new ApiError();
  }
}
