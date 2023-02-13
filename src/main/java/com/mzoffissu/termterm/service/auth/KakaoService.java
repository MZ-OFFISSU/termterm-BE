package com.mzoffissu.termterm.service.auth;

import com.mzoffissu.termterm.domain.auth.SocialLoginType;
import com.mzoffissu.termterm.domain.auth.Member;
import com.mzoffissu.termterm.dto.auth.TokenResponseDto;
import com.mzoffissu.termterm.dto.auth.KakaoMemberInfoDto;
import com.mzoffissu.termterm.dto.auth.MemberInfoDto;
import com.mzoffissu.termterm.exception.AuthorityExceptionType;
import com.mzoffissu.termterm.exception.BizException;
import com.mzoffissu.termterm.exception.InternalServerExceptionType;
import com.mzoffissu.termterm.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoService extends SocialAuthService{
    private static final Integer CONN_TIMEOUT = 15 * 1000;  // 15초
    private static final String KAKAO_TOKEN_REQUEST_URL = "https://kauth.kakao.com/oauth/token";
    private static final String KAKAO_MEMBERINFO_REQUEST_URL = "https://kapi.kakao.com/v2/user/me";

    @Value("${auth.kakao.client-id}")
    private String CLIENT_ID;

    @Value("${auth.kakao.redirect-uri}")
    private String REDIRECT_URI;

    private final MemberRepository memberRepository;

    /**
     * 인가 코드로 토큰 받기
     */
    @Override
    public TokenResponseDto getToken(String authorizationCode){
        URL url;
        HttpURLConnection urlConnection;
        try{
            url = new URL(KAKAO_TOKEN_REQUEST_URL);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setConnectTimeout(CONN_TIMEOUT);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=" + CLIENT_ID);
            sb.append("&redirect_uri=" + REDIRECT_URI);
            sb.append("&code=" + authorizationCode);

            bw.write(sb.toString());
            bw.flush();

            // 실제 서버로 Request 요청 하는 부분. (응답 코드를 받는다. 200 성공, 나머지 에러)
            Integer responseCode = urlConnection.getResponseCode();
            if(responseCode != 200){
                log.error("카카오 토큰 요청 오류 : {}", url);
                throw new BizException(AuthorityExceptionType.KAKAO_CONNECTION_ERROR);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            String result = "";
            while ((line = br.readLine()) != null) {
                result += line;
            }

            // json parsing
            JSONParser parser = new JSONParser();
            JSONObject elem = (JSONObject) parser.parse(result);

            return TokenResponseDto.builder()
                    .token_type(elem.get("token_type").toString())
                    .access_token(elem.get("access_token").toString())
                    .expires_in(elem.get("expires_in").toString())
                    .refresh_token(elem.get("refresh_token").toString())
                    .refresh_token_expires_in(elem.get("refresh_token_expires_in").toString())
                    .scope(elem.get("scope").toString())
                    .build();

        }catch (ParseException e){
            log.error("JSON 파싱 실패 : {}", e.getMessage());
            throw new BizException(InternalServerExceptionType.INTERNAL_SERVER_ERROR);
        }catch (MalformedURLException e){
            log.error("URL 형식 오류 : {}", KAKAO_TOKEN_REQUEST_URL);
            throw new BizException(InternalServerExceptionType.INTERNAL_SERVER_ERROR);
        }catch (IOException e){
            log.error("urlConnection 오류");
            throw new BizException(AuthorityExceptionType.KAKAO_CONNECTION_ERROR);
        }
    }

    /**
     * 받은 토큰을 이용하여 사용자 정보를 카카오 서버로부터 불러오기
     * name 권한 획득 시 .name(name)으로 바꿀 것
     */
    @Override
    public KakaoMemberInfoDto getMemberInfo(TokenResponseDto tokenResponse){
        String accessToken = tokenResponse.getAccess_token();
        URL url;
        KakaoMemberInfoDto memberInfo;

        try{
            url = new URL(KAKAO_MEMBERINFO_REQUEST_URL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

            urlConnection.getResponseCode();

            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            String res = "";
            while((line=br.readLine()) != null) {
                res += line;
            }

            // json parsing
            JSONParser parser = new JSONParser();
            JSONObject elem = (JSONObject) parser.parse(res);
            JSONObject kakaoAccount = (JSONObject) elem.get("kakao_account");
            JSONObject profile = (JSONObject) kakaoAccount.get("profile");

            String socialId = elem.get("id").toString();
//            String name = kakaoAccount.get("name").toString();
            String email = kakaoAccount.get("email").toString();

            String nickname = profile.get("nickname").toString();
            Boolean is_default_image = (Boolean) profile.get("is_default_image");
            String thumbnail_image_url = profile.get("thumbnail_image_url").toString();

            memberInfo = KakaoMemberInfoDto.builder()
                    .isDefaultImage(is_default_image)
                    .build();

            memberInfo.setSocialId(socialId);
            memberInfo.setName(nickname);
            memberInfo.setEmail(email);
            memberInfo.setNickname(nickname);
            memberInfo.setPicture(thumbnail_image_url);

        }catch (ParseException e){
            log.error("JSON 파싱 실패 : {}", e.getMessage());
            throw new BizException(InternalServerExceptionType.INTERNAL_SERVER_ERROR);
        }catch (MalformedURLException e){
            log.error("URL 형식 오류 : {}", KAKAO_MEMBERINFO_REQUEST_URL);
            throw new BizException(InternalServerExceptionType.INTERNAL_SERVER_ERROR);
        }catch (IOException e){
            log.error("urlConnection 오류");
            throw new BizException(AuthorityExceptionType.KAKAO_CONNECTION_ERROR);
        }
        return memberInfo;
    }

    /**
     * 회원 등록이 안 되어 있을 경우 회원가입
     */
    @Override
    public void signup(MemberInfoDto memberInfo) {
        String socialId = memberInfo.getSocialId();

        Boolean isRegistered = !memberRepository.findBySocialId(socialId).equals(Optional.empty());
        if (isRegistered) {
            return;
        }

//        String name = memberInfo.getName();
        String email = memberInfo.getEmail();
        String nickname = memberInfo.getNickname();
        String picture = memberInfo.getPicture();

        Member member = Member.builder()
                .socialId(socialId)
                .name(nickname)
                .email(email)
                .picture(picture)
                .socialLoginType(SocialLoginType.KAKAO)
                .build();
        memberRepository.save(member);
        log.info("회원가입 : {} ({})", member.getEmail(), member.getName());
    }
}
