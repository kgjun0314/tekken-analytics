package io.github.kgjun0314.tekken_analytics.common.exception;

import java.time.Instant;

public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String code,
        String message,
        String path
) {

    public static ErrorResponse of(
            ErrorCode errorCode,
            String path
    ) {

        return new ErrorResponse(
                Instant.now(),
                errorCode.getStatus().value(),
                errorCode.getStatus().getReasonPhrase(),
                errorCode.getCode(),
                errorCode.getMessage(),
                path
        );
    }
}