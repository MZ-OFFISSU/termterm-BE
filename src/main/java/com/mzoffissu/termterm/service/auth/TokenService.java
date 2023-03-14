package com.mzoffissu.termterm.service.auth;

import com.mzoffissu.termterm.domain.auth.Member;
import com.mzoffissu.termterm.domain.jwt.RefreshToken;
import com.mzoffissu.termterm.dto.jwt.TokenDto;
import com.mzoffissu.termterm.jwt.CustomSocialIdAuthToken;
import com.mzoffissu.termterm.jwt.TokenProvider;
import com.mzoffissu.termterm.repository.MemberRepository;
import com.mzoffissu.termterm.repository.RefreshTokenRepository;
import com.mzoffissu.termterm.response.exception.BizException;
import com.mzoffissu.termterm.response.exception.JwtExceptionType;
import com.mzoffissu.termterm.response.exception.MemberExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RefreshTokenRepository refreshTokenRepository;
    public static final String BEARER_PREFIX = "Bearer ";

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
}
