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
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final S3Uploader s3Uploader;
    public static final String BEARER_PREFIX = "Bearer ";
    public static final Long PM = CategoryType.PM.getId();
    public static final Long MARKETING = CategoryType.MARKETING.getId();
    public static final Long DEVELOPMENT = CategoryType.DEVELOPMENT.getId();
    public static final Long DESIGN = CategoryType.DESIGN.getId();
    public static final Long BUSINESS = CategoryType.BUSINESS.getId();
    public static final Long IT = CategoryType.IT.getId();

    /**
     * Username과 Password 대신 Member의 ID와 SocialID로 토큰 생성
     */
    @Transactional
    public TokenDto createToken(Member member, String memberSocialId){
        String memberId = member.getId().toString();

        CustomSocialIdAuthToken authenticationToken
                = new CustomSocialIdAuthToken(memberId, memberSocialId);

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = tokenProvider.createAccessToken(memberId, authentication);

        // 만약 해당 유저의 refreshToken이 이미 있다면 삭제하고 재생성
        if(refreshTokenRepository.existsByKey(memberId)){
            refreshTokenRepository.deleteRefreshToken(refreshTokenRepository.findByKey(memberId).orElseThrow(() -> new BizException(MemberExceptionType.NOT_FOUND_USER)));
        }

        String refreshToken = tokenProvider.createRefreshToken(memberId, authentication);
        refreshTokenRepository.saveRefreshToken(
                RefreshToken.builder()
                        .key(memberId)
                        .value(refreshToken)
                        .build()
        );

        return tokenProvider.createTokenDto(accessToken, refreshToken);
    }

    public Member findByEmailAndSocialType(String email, SocialLoginType socialLoginType){
        return memberRepository.findByEmailAndSocialType(email, socialLoginType)
                .orElseThrow(() -> new BizException(MemberExceptionType.NOT_FOUND_USER));
    }

    public MemberInfoDto getMemberInfo(String token) {
        Member member = getMemberByToken(token);

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

    private String resolveToken(String token) {
        if (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) {
            return token.substring(7);
        }

        return null;
    }

    public Member getMemberByToken(String token){
        String accessToken = resolveToken(token);
        String memberId = tokenProvider.getMemberIdByToken(accessToken);

        return memberRepository.findById(Long.parseLong(memberId))
                .orElseThrow(() -> new BizException(MemberExceptionType.NOT_FOUND_USER));
    }

    @Transactional
    public TokenDto reissue(String rfToken) {
        String refreshToken = resolveToken(rfToken);

        Integer refreshTokenFlag = tokenProvider.validateToken(refreshToken);

        // refreshToken을 검증하고 상황에 맞는 오류를 내보낸다.
        if(refreshTokenFlag == -1){
            throw new BizException(JwtExceptionType.MALFORMED_JWT);
        }else if(refreshTokenFlag == -2){
            throw new BizException(JwtExceptionType.REFRESH_TOKEN_EXPIRED);
        }else if(refreshTokenFlag == -3){
            throw new BizException(JwtExceptionType.UNSUPPORTED_JWT);
        }else if(refreshTokenFlag == -4){
            throw new BizException(JwtExceptionType.BAD_TOKEN);
        }

        // refreshToken에서 memberId 가져오기
        String memberId = tokenProvider.getMemberIdByToken(refreshToken);

        // repo 에서 id를 기반으로 refresh token을 가져온다.
        RefreshToken originRefreshToken = refreshTokenRepository.findByKey(memberId)
                .orElseThrow(() -> new BizException(MemberExceptionType.LOGOUT_MEMBER));

        // Refresh token 일치하는지 검사
        if(!originRefreshToken.getValue().equals(refreshToken)){
            throw new BizException(JwtExceptionType.BAD_TOKEN); // 토큰이 일치하지 않습니다.
        }

        // 새로운 토큰 생성
        Member member = memberRepository.findById(Long.parseLong(memberId))
                .orElseThrow(() -> new BizException(MemberExceptionType.NOT_FOUND_USER));
        String socialId = member.getSocialId();

        TokenDto tokenDto = createToken(member, socialId);
        originRefreshToken.updateValue(tokenDto.getRefreshToken());

        return tokenDto;
    }

    @Transactional
    public void logout(String token) {
        Member member = getMemberByToken(token);

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
    public MemberInfoDto updateMemberInfo(String token, MemberInfoUpdateRequestDto memberInfoUpdateRequestDto) {
        Member member = getMemberByToken(token);

        boolean isDuplicated = isNicknameDuplicatedExceptMe(member, memberInfoUpdateRequestDto.getNickname());
        if(isDuplicated){
            throw new BizException(MemberExceptionType.DUPLICATE_NICKNAME);
        }
        try {
            member.updateInfo(memberInfoUpdateRequestDto);
        }catch (Exception e){
            throw new BizException(MemberExceptionType.DUPLICATE_NICKNAME);
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
                .build();

    }

    @Transactional
    public void updateProfileImage(String accessToken, MultipartFile imageFile) {
        Member member = getMemberByToken(accessToken);
        String s3DirName = member.getId().toString() + member.getEmail().replace("@", "-");

        try{
            s3Uploader.upload(imageFile, s3DirName);
        }catch (Exception e){
            throw new BizException(S3UploadExceptionType.CANNOT_CONVERT_FILE);
        }



    }

    @Transactional
    public void updateCategories(String accessToken, MemberCategoriesRequestDto memberCategoriesRequestDto) {
        Member member = getMemberByToken(accessToken);

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
