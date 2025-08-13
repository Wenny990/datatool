package com.wnhuang.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据库异常
 *
 * @author wnhuang
 * @Package com.wnhuang.common.exception
 * @date 2025/8/10 15:19
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DatabaseException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    protected Integer errorCode;

    /**
     * 错误信息
     */
    protected String errorMsg;

    public DatabaseException() {
        super();
    }

    public DatabaseException(String message) {
        super(message);
        this.errorMsg = message;
    }

    public DatabaseException(ErrorEnum error) {
        super(error.getErrorMsg());
        this.errorCode = error.getErrorCode();
        this.errorMsg = error.getErrorMsg();
    }

    public DatabaseException(Throwable e) {
        super(e);
        this.errorMsg = e.getMessage();
    }

    @Override
    public String getMessage() {
        return errorMsg;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
