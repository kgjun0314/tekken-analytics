package io.github.kgjun0314.tekken_analytics.common.exception;

import lombok.Getter;

@Getter
public abstract class BusinessException
        extends RuntimeException {

    private final ErrorCode errorCode;

    protected BusinessException(
            ErrorCode errorCode
    ) {

        super(errorCode.getMessage());

        this.errorCode = errorCode;
    }
}