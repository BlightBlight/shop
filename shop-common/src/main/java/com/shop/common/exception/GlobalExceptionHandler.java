package com.shop.common.exception;

import javax.servlet.http.HttpServletRequest;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shop.common.result.CommonResult;

import lombok.extern.slf4j.Slf4j;

/**
 * 全局异常处理
 * DAO层异常：需要报告
 * Serviec层异常：需要报告
 * 服务异常（数据库）：需要报告
 * 系统异常（OOM）：需要报告
 * 参数校验异常：需要提示用户
 * 业务异常：显示状态页，但要求业务异常类有用户可读的说明
 */

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	/**
	 * 处理请求对象属性不满足校验规则的异常信息
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseBody
	public CommonResult<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		BindingResult result = e.getBindingResult();
		FieldError fieldError = result.getFieldError();
		log.debug("对象具体属性不符：" + fieldError.getDefaultMessage());
		return CommonResult.validateFailed(fieldError.getDefaultMessage());
	}
	
	/**
	 * 处理Service层返回异常
	 */
	@ExceptionHandler(value = ServiceException.class)
	@ResponseBody
	public CommonResult<String> ServiceException(ServiceException e) {
		log.debug("业务异常：" + e.getMessage());
		if (e.getErrorCode() != null) {
			return CommonResult.failed(e.getErrorCode());
		}
		return CommonResult.failed(e.getMessage());
	}
	
	/**
	 * 处理DAO层返回异常
	 */
	@ExceptionHandler(value = DAOException.class)
	@ResponseBody
	public CommonResult<String> handleDAOException(DAOException e) {
		log.error("DAO层异常：" + e);
		if (e.getErrorCode() != null) {
			return CommonResult.failed(e.getErrorCode());
		}
		return CommonResult.internalServerFailed();
	}

	/**
	 * 处理空指针的异常
	 */
	@ExceptionHandler(value =NullPointerException.class)
	@ResponseBody
	public CommonResult<String> exceptionHandler(HttpServletRequest req, NullPointerException e){
		log.debug("空指针异常:" + e);
		
		return CommonResult.internalServerFailed();
	}
	
	/**
	 * 处理其他未知异常
	 */
	@ExceptionHandler(value =Exception.class)
	@ResponseBody
	public CommonResult<String> exceptionHandler(HttpServletRequest req, Exception e){
		log.error("发生未知异常：" + e);
		
    	return CommonResult.internalServerFailed();
	}
}
