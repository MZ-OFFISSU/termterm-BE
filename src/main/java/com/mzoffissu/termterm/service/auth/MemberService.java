package com.mzoffissu.termterm.service.auth;

import com.mzoffissu.termterm.domain.auth.Member;
import com.mzoffissu.termterm.domain.auth.SocialLoginType;
import com.mzoffissu.termterm.domain.category.Category;
import com.mzoffissu.termterm.domain.category.CategoryType;
import com.mzoffissu.termterm.domain.jwt.RefreshToken;
import com.mzoffissu.termterm.dto.auth.MemberInfoDto;
import com.mzoffissu.termterm.dto.category.CategoryDto;
import com.mzoffissu.termterm.dto.jwt.TokenDto;
import com.mzoffissu.termterm.dto.member.MemberInfoUpdateRequestDto;
import com.mzoffissu.termterm.dto.member.MemberCategoriesRequestDto;
import com.mzoffissu.termterm.response.exception.BizException;
import com.mzoffissu.termterm.response.exception.JwtExceptionType;
import com.mzoffissu.termterm.response.exception.MemberExceptionType;
import com.mzoffissu.termterm.response.exception.S3UploadExceptionType;
import com.mzoffissu.termterm.jwt.CustomSocialIdAuthToken;
import com.mzoffissu.termterm.jwt.TokenProvider;
import com.mzoffissu.termterm.repository.CategoryRepository;
import com.mzoffissu.termterm.repository.MemberRepository;
import com.mzoffissu.termterm.repository.RefreshTokenRepository;
import com.mzoffissu.termterm.service.s3.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final TokenService tokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final S3Uploader s3Uploader;
    public static final Long PM = CategoryType.PM.getId();
    public static final Long MARKETING = CategoryType.MARKETING.getId();
    public static final Long DEVELOPMENT = CategoryType.DEVELOPMENT.getId();
    public static final Long DESIGN = CategoryType.DESIGN.getId();
    public static final Long BUSINESS = CategoryType.BUSINESS.getId();
    public static final Long IT = CategoryType.IT.getId();



    public Member findByEmailAndSocialType(String email, SocialLoginType socialLoginType){
        return memberRepository.findByEmailAndSocialType(email, socialLoginType)
                .orElseThrow(() -> new BizException(MemberExceptionType.NOT_FOUND_USER));
    }

    public MemberInfoDto getMemberInfo(Member member) {
        Set<Category> memberCategories = member.getCategories();

        List<CategoryDto> categoryDtos = new ArrayList<>();
        for(Category category : memberCategories){
            CategoryDto categoryDto = CategoryDto.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .build();

            categoryDtos.add(categoryDto);
        }


        return MemberInfoDto.builder()
                .memberId(member.getId())
                .domain(member.getDomain())
                .email(member.getEmail())
                .introduction(member.getIntroduction())
                .job(member.getJob())
                .name(member.getName())
                .nickname(member.getNickname())
                .point(member.getPoint())
                .profileImage(member.getProfileImg())
                .yearCareer(member.getYearCareer())
                .categories(categoryDtos)
                .build();
    }



    @Transactional
    public void logout(Member member) {
        RefreshToken refreshToken = refreshTokenRepository.findByKey(member.getId().toString())
                .orElseThrow(() -> new BizException(MemberExceptionType.LOGOUT_MEMBER));
        refreshTokenRepository.deleteRefreshToken(refreshToken);
    }

    public boolean isNicknameDuplicated(String nickname){
        return memberRepository.existsByNicknameCustom(nickname);
    }

    protected boolean isNicknameDuplicatedExceptMe(Member member, String newNickname){
        return memberRepository.existsByNicknameExceptMeCustom(member, newNickname);
    }
    @Transactional
    public MemberInfoDto updateMemberInfo(Member member, MemberInfoUpdateRequestDto memberInfoUpdateRequestDto) {
        boolean isDuplicated = isNicknameDuplicatedExceptMe(member, memberInfoUpdateRequestDto.getNickname());
        if(isDuplicated){
            throw new BizException(MemberExceptionType.DUPLICATE_NICKNAME);
        }
        try {
            member.updateInfo(memberInfoUpdateRequestDto);
        }catch (Exception e){
            throw new BizException(MemberExceptionType.DUPLICATE_NICKNAME);
        }

        Set<Category> memberCategories = member.getCategories();
        List<CategoryDto> categoryDtos = new ArrayList<>();
        for(Category category : memberCategories){
            CategoryDto categoryDto = CategoryDto.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .build();

            categoryDtos.add(categoryDto);
        }

        return MemberInfoDto.builder()
                .memberId(member.getId())
                .name(member.getName())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .profileImage(member.getProfileImg())
                .job(member.getJob())
                .domain(member.getDomain())
                .introduction(member.getIntroduction())
                .point(member.getPoint())
                .yearCareer(member.getYearCareer())
                .introduction(member.getIntroduction())
                .categories(categoryDtos)
                .build();
    }

    @Transactional
    public void updateProfileImage(Member member, MultipartFile imageFile) {
        String s3DirName = member.getId().toString() + member.getEmail().replace("@", "-");

        try{
            s3Uploader.upload(imageFile, s3DirName);
        }catch (Exception e){
            throw new BizException(S3UploadExceptionType.CANNOT_CONVERT_FILE);
        }



    }

    @Transactional
    public void updateCategories(Member member, MemberCategoriesRequestDto memberCategoriesRequestDto) {
        Set<Category> newCategories = new HashSet<>();

        if (memberCategoriesRequestDto.isPm()) newCategories.add(categoryRepository.getById(PM));
        if (memberCategoriesRequestDto.isMarketing()) newCategories.add(categoryRepository.getById(MARKETING));
        if (memberCategoriesRequestDto.isDevelopment()) newCategories.add(categoryRepository.getById(DEVELOPMENT));
        if (memberCategoriesRequestDto.isDesign()) newCategories.add(categoryRepository.getById(DESIGN));
        if (memberCategoriesRequestDto.isBusiness()) newCategories.add(categoryRepository.getById(BUSINESS));
        if (memberCategoriesRequestDto.isIt()) newCategories.add(categoryRepository.getById(IT));

        member.updateCategories(newCategories);
    }
}
