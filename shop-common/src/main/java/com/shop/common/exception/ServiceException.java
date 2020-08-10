package com.shop.common.exception;

import com.shop.common.result.IErrorCode;

/**
 * 自定义Service层异常
 */
public class ServiceException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private IErrorCode errorCode;

    public ServiceException(IErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public IErrorCode getErrorCode() {
        return errorCode;
    }
}
