package com.shop.common.exception;

import com.shop.common.result.IErrorCode;

/**
 * DAO层异常
 */

/*
 * 记录异常发送原因、时间即可
 * 由于DAOException是抛给Service层的
 * Service层对DAO具体什么异常不感兴趣，所以使用一个RunTimeException包含大部分DAO层异常足够
 * 具体DAO异常是什么留给开发人员看日志慢慢分析
 */

public class DAOException extends RuntimeException {
private static final long serialVersionUID = 1L;
	
	private IErrorCode errorCode;	//这个可有可无，还是保留下来吧

    public DAOException(IErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public DAOException(String message) {
        super(message);
    }

    public DAOException(Throwable cause) {
        super(cause);
    }

    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }

    public IErrorCode getErrorCode() {
        return errorCode;
    }
}
