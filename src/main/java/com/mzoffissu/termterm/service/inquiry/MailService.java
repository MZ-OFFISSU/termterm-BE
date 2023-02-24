package com.mzoffissu.termterm.service.inquiry;

import com.mzoffissu.termterm.dto.inquiry.InquiryRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;

    public void sendAcceptMail(InquiryRequestDto inquiryRequestDto){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("termterm.contact@gmail.com");
        message.setTo(inquiryRequestDto.getEmail());
        message.setSubject("[텀텀] 고객님의 문의가 정상적으로 접수되었습니다.");
        message.setText("내용");
        mailSender.send(message);
    }

}
