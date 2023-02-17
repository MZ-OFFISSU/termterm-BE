package com.mzoffissu.termterm.repository;

import com.mzoffissu.termterm.domain.inquiry.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;


public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
}
