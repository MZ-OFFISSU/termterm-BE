package com.mzoffissu.termterm.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberCategoriesRequestDto {
    private boolean pm;
    private boolean marketing;
    private boolean development;
    private boolean design;
    private boolean business;
    private boolean it;
}
