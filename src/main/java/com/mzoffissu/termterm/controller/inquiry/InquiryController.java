package com.mzoffissu.termterm.controller.inquiry;

import com.mzoffissu.termterm.domain.inquiry.Inquiry;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class InquiryController {
    private final InquiryService inquiryService;

    @PostMapping("/inquiry/save")
    public @ResponseBody ResponseEntity saveInquiry(@RequestBody InquiryRequestDto inquiryRequestDto){
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

    @GetMapping("/inquiry/delete/{id}")
    public @ResponseBody ResponseEntity deleteInquiry(@PathVariable("id") Long id){
        try{
            inquiryService.deleteInquiry(id);
            log.info("문의사항 삭제 - {}", id);
            return new ResponseEntity<>(DefaultResponse.create(HttpStatus.NO_CONTENT.value(), ResponseMessage.INQUIRY_DELETED), HttpStatus.NO_CONTENT);
        }catch (BizException e){
            log.error(e.getMessage());
            return new ResponseEntity<>(DefaultResponse.create(e.getBaseExceptionType().getHttpStatus().value(), e.getMessage()), e.getBaseExceptionType().getHttpStatus());
        }
        catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(DefaultResponse.create(HttpStatus.INTERNAL_SERVER_ERROR.value(), InternalServerExceptionType.INTERNAL_SERVER_ERROR.getMessage()), InternalServerExceptionType.INTERNAL_SERVER_ERROR.getHttpStatus());
        }
    }

    @GetMapping("/inquiry/detail/{id}")
    public String inquiryDetail(@PathVariable("id") Long id, Model model){
        Inquiry inquiry = inquiryService.findById(id);
        model.addAttribute("inquiry", inquiry);
        log.info("문의 상세 페이지 - {}", inquiry.getId());
        return "inquiryDetail";
    }

    @GetMapping("/inquiry/proceed/{id}")
    public @ResponseBody ResponseEntity proceedInquiry(@PathVariable("id") Long id){
        try{
            inquiryService.proceedInquiry(id);
            log.info("문의사항 처리 - {}", id);
            return new ResponseEntity<>(DefaultResponse.create(HttpStatus.NO_CONTENT.value(), ResponseMessage.INQUIRY_DELETED), HttpStatus.NO_CONTENT);
        }catch (BizException e){
            log.error(e.getMessage());
            return new ResponseEntity<>(DefaultResponse.create(e.getBaseExceptionType().getHttpStatus().value(), e.getMessage()), e.getBaseExceptionType().getHttpStatus());
        }
        catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(DefaultResponse.create(HttpStatus.INTERNAL_SERVER_ERROR.value(), InternalServerExceptionType.INTERNAL_SERVER_ERROR.getMessage()), InternalServerExceptionType.INTERNAL_SERVER_ERROR.getHttpStatus());
        }
    }

    @GetMapping("/inquiry/hold/{id}")
    public @ResponseBody ResponseEntity holdInquiry(@PathVariable("id") Long id){
        try{
            inquiryService.holdInquiry(id);
            log.info("문의사항 대기 중으로 변환 - {}", id);
            return new ResponseEntity<>(DefaultResponse.create(HttpStatus.NO_CONTENT.value(), ResponseMessage.INQUIRY_DELETED), HttpStatus.NO_CONTENT);
        }catch (BizException e){
            log.error(e.getMessage());
            return new ResponseEntity<>(DefaultResponse.create(e.getBaseExceptionType().getHttpStatus().value(), e.getMessage()), e.getBaseExceptionType().getHttpStatus());
        }
        catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(DefaultResponse.create(HttpStatus.INTERNAL_SERVER_ERROR.value(), InternalServerExceptionType.INTERNAL_SERVER_ERROR.getMessage()), InternalServerExceptionType.INTERNAL_SERVER_ERROR.getHttpStatus());
        }
    }
}