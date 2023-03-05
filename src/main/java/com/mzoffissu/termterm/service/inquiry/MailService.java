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

        String mailContent = "안녕하세요. termterm입니다.\n" +
                "\n" +
                "고객님의 문의가 정상적으로 접수되었습니다.\n" +
                "\n" +
                "휴일을 제외한 평일 하루이내에 답변을 드리는 점에 대해 양해 부탁드립니다.\n" +
                "\n" +
                "순차적으로 확인 후 최대한 빠른 시일내에 답변해드리도록 하겠습니다.\n" +
                "\n" +
                "휴일을 제외한 하루가 지나지 않아도 답변이 오지 않는다면,\n" +
                "\n" +
                "스팸 메일함에 답변이 있을 수 있으니 스팸 메일함을 확인해주세요.\n" +
                "\n" +
                "궁금하신 점이 있으시면 언제든지 저희 termterm으로 연락주시기 바랍니다.\n" +
                "\n" +
                "감사합니다.";

        message.setFrom("termterm.contact@gmail.com");
        message.setTo(inquiryRequestDto.getEmail());
        message.setSubject("[텀텀] 고객님의 문의가 정상적으로 접수되었습니다.");
        message.setText(mailContent);
        mailSender.send(message);
    }

}
