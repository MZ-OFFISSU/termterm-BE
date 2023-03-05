package com.mzoffissu.termterm.aspect;

import com.mzoffissu.termterm.dto.DefaultResponse;
import com.mzoffissu.termterm.dto.inquiry.InquiryRequestDto;
import com.mzoffissu.termterm.service.inquiry.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class MailAspect {
    private final MailService mailService;

    @AfterReturning(
            pointcut = "execution(* com.mzoffissu.termterm.controller.inquiry.InquiryController.saveInquiry(com.mzoffissu.termterm.dto.inquiry.InquiryRequestDto))",
            returning = "returned"
    )
    public void afterReturnSaveInquiry(ResponseEntity returned){
        DefaultResponse body = (DefaultResponse) returned.getBody();
        InquiryRequestDto inquiryRequestDto = (InquiryRequestDto) body.getData();
        mailService.sendAcceptMail(inquiryRequestDto);
    }
}
