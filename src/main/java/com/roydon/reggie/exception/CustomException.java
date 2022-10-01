package com.roydon.reggie.exception;

/**
 * 自定义业务异常类
 * throw new CustomException("自定义异常处理信息");
 */
public class CustomException extends RuntimeException {
    public CustomException(String message) {
        super(message);
    }
}
