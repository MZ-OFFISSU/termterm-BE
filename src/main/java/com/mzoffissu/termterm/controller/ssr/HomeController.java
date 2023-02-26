package com.mzoffissu.termterm.controller.ssr;

import com.mzoffissu.termterm.domain.inquiry.Inquiry;
import com.mzoffissu.termterm.service.inquiry.InquiryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {
    private final InquiryService inquiryService;

    @GetMapping("/")
    public String inquiryList(@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable, Model model){
        Page<Inquiry> inquiries = inquiryService.findAll(pageable);
        model.addAttribute("inquiries", inquiries);
        model.addAttribute("maxPage", 20);
        log.info("문의사항 페이지 접속");
        return "index";
    }

}
