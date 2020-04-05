package com.zeroone.tenancy.hibernate.rest;

import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

/**
 * 局部异常
 * @author zero-one.lu
 * @since 2020-04-05
 */
public abstract class ExceptionHandlerResource {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerResource.class);


    /**
     * 业务异常拦截器
     *
     */
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    public String bizExceptionHandler(IllegalStateException e) {
        logger.error(e.getMessage(), e);
        return e.getMessage();
    }



    /**
     * 全局异常拦截器
     */
    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    public String bindExceptionHandler(Exception e) {
        logger.error(e.getMessage(), e);
        return getAllErrors(e).get(0).getDefaultMessage();
    }

    /**
     * 全局异常拦截器
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    public String exceptionHandler(Exception e) {
        logger.error(e.getMessage(), e);
        return getCauseString(e);
    }

    private String getCauseString(Exception e) {
        return Throwables.getRootCause(e).getMessage();
    }

    private List<ObjectError> getAllErrors(Exception e) {

        if (e instanceof BindException) {
            return ((BindException) e).getAllErrors();
        } else {
            return ((MethodArgumentNotValidException) e).getBindingResult().getAllErrors();
        }
    }
}
