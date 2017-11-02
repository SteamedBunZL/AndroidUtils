package com.clean.spaceplus.cleansdk.base.exception;

/**
 * @author dongdong.huang
 * @Description:失败异常处理类
 * @date 2016/4/28 20:31
 * @copyright TCL-MIG
 */
public class FailException extends RuntimeException{
    public FailException() {
    }

    public FailException(String detailMessage) {
        super(detailMessage);
    }

    public FailException(Throwable throwable) {
        super(throwable);
    }

    public FailException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
