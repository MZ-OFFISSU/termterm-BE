package com.mzoffissu.termterm.controller.inquiry;

import com.mzoffissu.termterm.domain.inquiry.Inquiry;
import com.mzoffissu.termterm.response.DefaultResponse;
import com.mzoffissu.termterm.dto.inquiry.InquiryRequestDto;
import com.mzoffissu.termterm.response.success.InquirySuccessType;
import com.mzoffissu.termterm.service.inquiry.InquiryService;
import com.mzoffissu.termterm.service.inquiry.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v1")
public class InquiryController {
    private final InquiryService inquiryService;
    @GetMapping("/inquiry")
    public String inquiryList(@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable, Model model){
        Page<Inquiry> inquiries = inquiryService.findAll(pageable);
        model.addAttribute("inquiries", inquiries);
        model.addAttribute("maxPage", 20);
        log.info("문의사항 페이지 접속");
        return "inquiry/inquiry";
    }
    @PostMapping("/inquiry/save")
    public @ResponseBody ResponseEntity saveInquiry(@RequestBody InquiryRequestDto inquiryRequestDto){
        inquiryService.saveInquiry(inquiryRequestDto.trimAll());

        log.info("문의사항 접수 - {}", inquiryRequestDto.getEmail());
        return new ResponseEntity<>(DefaultResponse.create(InquirySuccessType.INQUIRY_ACCEPTED, inquiryRequestDto), InquirySuccessType.INQUIRY_ACCEPTED.getHttpStatus());
    }

    @GetMapping("/inquiry/delete/{id}")
    public @ResponseBody ResponseEntity deleteInquiry(@PathVariable("id") Long id){
        inquiryService.deleteInquiry(id);
        log.info("문의사항 삭제 - {}", id);
        return new ResponseEntity<>(DefaultResponse.create(InquirySuccessType.INQUIRY_DELETED), InquirySuccessType.INQUIRY_DELETED.getHttpStatus());
    }

    @GetMapping("/inquiry/detail/{id}")
    public String inquiryDetail(@PathVariable("id") Long id, Model model){
        Inquiry inquiry = inquiryService.findById(id);
        model.addAttribute("inquiry", inquiry);
        log.info("문의 상세 페이지 - {}", inquiry.getId());
        return "inquiry/inquiryDetail";
    }

    @GetMapping("/inquiry/proceed/{id}")
    public @ResponseBody ResponseEntity proceedInquiry(@PathVariable("id") Long id){
        inquiryService.proceedInquiry(id);
        log.info("문의사항 처리 - {}", id);
        return new ResponseEntity<>(DefaultResponse.create(InquirySuccessType.INQUIRY_PROCEED), InquirySuccessType.INQUIRY_PROCEED.getHttpStatus());
    }

    @GetMapping("/inquiry/hold/{id}")
    public @ResponseBody ResponseEntity holdInquiry(@PathVariable("id") Long id){
        inquiryService.holdInquiry(id);
        log.info("문의사항 대기 중으로 변환 - {}", id);
        return new ResponseEntity<>(DefaultResponse.create(InquirySuccessType.INQUIRY_HOLD), InquirySuccessType.INQUIRY_HOLD.getHttpStatus());
    }
}
