package io.github.kgjun0314.tekken_analytics.common.exception;

public class CharacterNotFoundException
        extends BusinessException {

    public CharacterNotFoundException() {
        super(ErrorCode.CHARACTER_NOT_FOUND);
    }
}