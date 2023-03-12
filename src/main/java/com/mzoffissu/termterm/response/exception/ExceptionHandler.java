package com.mzoffissu.termterm.response.exception;

import com.mzoffissu.termterm.response.DefaultResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(BizException.class)
    public ResponseEntity bizException(BizException e){
        return new ResponseEntity<>(DefaultResponse.create(e.getBaseExceptionType()), e.getBaseExceptionType().getHttpStatus());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public ResponseEntity notResolvedException(Exception e){
        return new ResponseEntity<>(DefaultResponse.create(InternalServerExceptionType.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
