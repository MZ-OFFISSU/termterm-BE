package com.mzoffissu.termterm.controller.inquiry;

import com.mzoffissu.termterm.dto.DefaultResponse;
import com.mzoffissu.termterm.dto.inquiry.InquiryRequestDto;
import com.mzoffissu.termterm.exception.BizException;
import com.mzoffissu.termterm.exception.InternalServerExceptionType;
import com.mzoffissu.termterm.service.inquiry.InquiryService;
import com.mzoffissu.termterm.vo.ResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class InquiryController {
    private final InquiryService inquiryService;

    @PostMapping("/inquiry/save")
    private ResponseEntity saveInquiry(@RequestBody InquiryRequestDto inquiryRequestDto){
        try {
            inquiryService.saveInquiry(inquiryRequestDto);
            log.info("문의사항 접수 - {}", inquiryRequestDto.getEmail());
            return new ResponseEntity<>(DefaultResponse.create(HttpStatus.CREATED.value(), ResponseMessage.INQUIRY_ACCEPTED), HttpStatus.CREATED);
        }
        catch (BizException e){
            log.error(e.getMessage());
            return new ResponseEntity<>(DefaultResponse.create(e.getBaseExceptionType().getHttpStatus().value(), e.getMessage()), e.getBaseExceptionType().getHttpStatus());
        }
        catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(DefaultResponse.create(HttpStatus.INTERNAL_SERVER_ERROR.value(), InternalServerExceptionType.INTERNAL_SERVER_ERROR.getMessage()), InternalServerExceptionType.INTERNAL_SERVER_ERROR.getHttpStatus());
        }
    }
}
