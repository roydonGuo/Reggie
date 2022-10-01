package com.roydon.reggie.exception;

import com.roydon.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * Created by Intellij IDEA
 * Author: yi cheng
 * Date: 2022/9/27
 * Time: 19:07
 **/
@Slf4j
@ControllerAdvice(annotations = {
        RestController.class, Controller.class
})
@ResponseBody
public class GlobalExceptionHandler {

    /**
     * 全局异常处理
     *
     * @param ex SQLIntegrityConstraintViolationException
     * @return R.error(" 自定义异常提示信息 ")
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        log.error(ex.getMessage());

        if (ex.getMessage().contains("Duplicate entry")) {
            return R.error(ex.getMessage().split(" ")[2] + "也存在");
        }

        return R.error("未知错误");
    }

    /**
     * 异常处理方法
     *
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex) {
        log.error(ex.getMessage());

        return R.error(ex.getMessage());
    }

}
