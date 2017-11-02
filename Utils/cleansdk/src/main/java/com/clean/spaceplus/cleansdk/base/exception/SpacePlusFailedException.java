package com.clean.spaceplus.cleansdk.base.exception;

/**
 * @author liangni
 * @Description:运行异常
 * @date 2016/4/23 13:45
 * @copyright TCL-MIG
 */
public class SpacePlusFailedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SpacePlusFailedException() {
    }

    public SpacePlusFailedException(String detailMessage) {
        super(detailMessage);
    }

    public SpacePlusFailedException(Throwable throwable) {
        super(throwable);
    }

    public SpacePlusFailedException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
