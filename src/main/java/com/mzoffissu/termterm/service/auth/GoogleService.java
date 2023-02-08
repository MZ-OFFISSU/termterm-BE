package com.mzoffissu.termterm.service.auth;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mzoffissu.termterm.domain.auth.Role;
import com.mzoffissu.termterm.domain.auth.SocialLoginType;
import com.mzoffissu.termterm.domain.auth.User;
import com.mzoffissu.termterm.dto.auth.GoogleUserInfoDto;
import com.mzoffissu.termterm.dto.auth.TokenResponseDto;
import com.mzoffissu.termterm.dto.auth.UserInfoDto;
import com.mzoffissu.termterm.exception.AuthorityExceptionType;
import com.mzoffissu.termterm.exception.BizException;
import com.mzoffissu.termterm.exception.InternalServerExceptionType;
import com.mzoffissu.termterm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleService extends SocialAuthService{
    private static final String GOOGLE_TOKEN_REQUEST_URL = "https://oauth2.googleapis.com";

    @Value("${auth.google.client-id}")
    private String CLIENT_ID;

    @Value("${auth.google.client-secret}")
    private String CLIENT_SECRET;

    @Value("${auth.google.redirect-uri}")
    private String REDIRECT_URI;

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    /**
     * 인가 코드로 토큰 받기
     */
    @Override
    public TokenResponseDto getToken(String code) {
        Map<String, Object> params = new HashMap<>();
        params.put("grant_type", "authorization_code");
        params.put("code", code);
        params.put("client_id", CLIENT_ID);
        params.put("client_secret", CLIENT_SECRET);
        params.put("redirect_uri", REDIRECT_URI);

        JSONParser parser;
        JSONObject elem;

        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(GOOGLE_TOKEN_REQUEST_URL + "/token", params, String.class);
            if (responseEntity.getStatusCode() != HttpStatus.OK) {
                log.error("구글 API 연결 실패 : code = {}", code);
                throw new BizException(AuthorityExceptionType.GOOGLE_CONNECTION_ERROR);
            }
            String responseBody = responseEntity.getBody();

            parser = new JSONParser();
            elem = (JSONObject) parser.parse(responseBody);

            return TokenResponseDto.builder()
                    .token_type(elem.get("token_type").toString())
                    .access_token(elem.get("access_token").toString())
                    .expires_in(elem.get("expires_in").toString())
                    .refresh_token(elem.get("refresh_token").toString())
                    .scope(elem.get("scope").toString())
                    .id_token(elem.get("id_token").toString())
                    .build();

        } catch (ParseException e){
            log.error("JSON 파싱 실패 : {}", e.getMessage());
            throw new BizException(InternalServerExceptionType.INTERNAL_SERVER_ERROR);
        } catch (Exception e){
            log.error("구글 API 연결 실패 : code = {}", code);
            throw new BizException(AuthorityExceptionType.GOOGLE_CONNECTION_ERROR);
        }
    }

    /**
     * 받은 토큰을 이용하여 사용자 정보를 구글 서버로부터 불러오기
     */
    @Override
    public GoogleUserInfoDto getUserInfo(TokenResponseDto tokenResponse){
        String idToken = tokenResponse.getId_token();
        String requestUrl = UriComponentsBuilder.fromHttpUrl(GOOGLE_TOKEN_REQUEST_URL + "/tokeninfo").queryParam("id_token", idToken).toUriString();
        GoogleUserInfoDto userInfo;
        JSONParser parser;
        JSONObject elem;
        String resultJson;

        try {
            resultJson = restTemplate.getForObject(requestUrl, String.class);

            parser = new JSONParser();
            elem = (JSONObject) parser.parse(resultJson);


            userInfo = GoogleUserInfoDto.builder()
                    .iss(elem.get("iss").toString())
                    .azp(elem.get("azp").toString())
                    .aud(elem.get("aud").toString())
                    .iat(elem.get("iat").toString())
                    .exp(elem.get("exp").toString())
                    .alg(elem.get("alg").toString())
                    .kid(elem.get("kid").toString())
                    .typ(elem.get("typ").toString())
                    .email_verified(elem.get("email_verified").toString())
                    .at_hash(elem.get("at_hash").toString())
                    .given_name(elem.get("given_name").toString())
                    .family_name(elem.get("family_name").toString())
                    .locale(elem.get("locale").toString())
                    .build();
            userInfo.setName(elem.get("name").toString());
            userInfo.setEmail(elem.get("email").toString());
            userInfo.setNickname(elem.get("name").toString());
            userInfo.setPicture(elem.get("picture").toString());
            userInfo.setSocialId(elem.get("sub").toString());

//            ObjectMapper objectMapper = new ObjectMapper();
//                if(resultJson != null){
//                    GoogleUserInfoDto userInfoDto = objectMapper.readValue(resultJson, new TypeReference<GoogleUserInfoDto>() {});
//                    return userInfoDto;
//                }
//                else{
//                    throw new Exception("Google OAuth failed!");
//                }
            return userInfo;
        }catch (ParseException e){
            log.error("JSON 파싱 실패 : {}", e.getMessage());
            throw new BizException(InternalServerExceptionType.INTERNAL_SERVER_ERROR);
        }catch (Exception e) {
            log.error("구글 API 연결 실패 : id_token = {}", idToken);
            log.error("구글 API 연결 실패 : {}", e.getMessage());
            throw new BizException(AuthorityExceptionType.GOOGLE_CONNECTION_ERROR);
        }
    }

    /**
     * 회원 등록이 안 되어 있을 경우 회원가입
     */
    @Override
    public void signup(UserInfoDto userInfo) {
        String socialId = userInfo.getSocialId();

        Boolean isRegistered = !userRepository.findBySocialId(socialId).equals(Optional.empty());
        if (isRegistered) {
            return;
        }

        String name = userInfo.getName();
        String email = userInfo.getEmail();
        String picture = userInfo.getPicture();

        User user = User.builder()
                .socialId(socialId)
                .name(name)
                .email(email)
                .picture(picture)
                .role(Role.USER)
                .socialLoginType(SocialLoginType.GOOGLE)
                .build();
        userRepository.save(user);
        log.info("회원가입 : {} ({})", user.getEmail(), user.getName());
    }
}
