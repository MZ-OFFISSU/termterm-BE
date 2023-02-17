package com.mzoffissu.termterm.service.inquiry;

import com.mzoffissu.termterm.domain.inquiry.Inquiry;
import com.mzoffissu.termterm.domain.inquiry.InquiryStatus;
import com.mzoffissu.termterm.domain.inquiry.InquiryType;
import com.mzoffissu.termterm.dto.inquiry.InquiryRequestDto;
import com.mzoffissu.termterm.exception.BizException;
import com.mzoffissu.termterm.exception.InquiryExceptionType;
import com.mzoffissu.termterm.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InquiryService {
    private final InquiryRepository inquiryRepository;

    @Transactional(readOnly = false)
    public void saveInquiry(InquiryRequestDto inquiryRequestDto) {
        InquiryType inquiryType;
        try {
            inquiryType = InquiryType.getInquiryType(inquiryRequestDto.getType());
        }catch (IllegalArgumentException e){
            throw new BizException(InquiryExceptionType.INVALID_INQUIRY_TYPE);
        }

        Inquiry inquiry = Inquiry.builder()
                .email(inquiryRequestDto.getEmail())
                .content(inquiryRequestDto.getContent())
                .status(InquiryStatus.A)
                .type(inquiryType)
                .build();

        inquiryRepository.save(inquiry);
    }
}
