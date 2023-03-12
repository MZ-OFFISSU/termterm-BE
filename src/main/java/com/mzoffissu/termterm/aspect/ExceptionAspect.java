package com.mzoffissu.termterm.aspect;

import com.mzoffissu.termterm.response.DefaultResponse;
import com.mzoffissu.termterm.response.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ExceptionAspect {

    @AfterThrowing(pointcut = "execution(* com.mzoffissu.termterm.*.*.*(..))", throwing = "e")
    public ResponseEntity afterThrowingException(BizException e){
        log.error(e.getMessage());
        return new ResponseEntity<>(DefaultResponse.create(e.getBaseExceptionType()), e.getBaseExceptionType().getHttpStatus());
    }
}
