package io.github.kgjun0314.tekken_analytics.common.exception;

public class PlayerNotFoundException
        extends BusinessException {

    public PlayerNotFoundException() {
        super(ErrorCode.PLAYER_NOT_FOUND);
    }
}