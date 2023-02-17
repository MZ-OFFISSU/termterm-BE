package com.mzoffissu.termterm.service.inquiry;

import com.mzoffissu.termterm.domain.inquiry.Inquiry;
import com.mzoffissu.termterm.domain.inquiry.InquiryStatus;
import com.mzoffissu.termterm.domain.inquiry.InquiryType;
import com.mzoffissu.termterm.repository.InquiryRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
class InquiryServiceTest {
    @Autowired
    InquiryRepository inquiryRepository;

    @Test
    @DisplayName("문의사항이 잘 저장된다.")
    public void goodInquiry() throws Exception{
        //given
        String email = "email@email.com";
        String content = "content";

        inquiryRepository.save(Inquiry.builder()
                .email(email)
                .content(content)
                .status(InquiryStatus.A)
                .type(InquiryType.OTHER)
                .build());

        //when
        Inquiry inquiry = inquiryRepository.getById(Long.parseLong("1"));

        //then
        assertThat(inquiry.getEmail()).isEqualTo(email);
        assertThat(inquiry.getContent()).isEqualTo(content);
        assertThat(inquiry.getStatus()).isEqualTo(InquiryStatus.A);
        assertThat(inquiry.getType()).isEqualTo(InquiryType.OTHER);
    }


}