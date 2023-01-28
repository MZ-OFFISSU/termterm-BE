package com.mzoffissu.termterm.service.auth;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mzoffissu.termterm.domain.auth.Role;
import com.mzoffissu.termterm.domain.auth.SocialLoginType;
import com.mzoffissu.termterm.domain.auth.User;
import com.mzoffissu.termterm.dto.auth.GoogleUserInfoDto;
import com.mzoffissu.termterm.dto.auth.TokenResponseDto;
import com.mzoffissu.termterm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoogleService {
    private static final String GOOGLE_TOKEN_REQUEST_URL = "https://oauth2.googleapis.com";
    private static final String GOOGLE_USERINFO_REQUEST_URL = "https://people.googleapis.com/v1/people/me?personFields=names";

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
    public TokenResponseDto getToken(String code) throws IOException {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("grant_type", "authorization_code");
            params.put("code", code);
            params.put("client_id", CLIENT_ID);
            params.put("client_secret", CLIENT_SECRET);
            params.put("redirect_uri", REDIRECT_URI);

            ResponseEntity<String> responseEntity = restTemplate.postForEntity(GOOGLE_TOKEN_REQUEST_URL + "/token", params, String.class);
            if (responseEntity.getStatusCode() != HttpStatus.OK) {
                throw new IOException("Google로부터 토큰을 받아오지 못했습니다.");
            }
            String responseBody = responseEntity.getBody();

            // json parsing
            JSONParser parser = new JSONParser();
            JSONObject elem = (JSONObject) parser.parse(responseBody);

            TokenResponseDto response = TokenResponseDto.builder()
                    .token_type(elem.get("token_type").toString())
                    .access_token(elem.get("access_token").toString())
                    .expires_in(elem.get("expires_in").toString())
                    .refresh_token(elem.get("refresh_token").toString())
                    .scope(elem.get("scope").toString())
                    .id_token(elem.get("id_token").toString())
                    .build();

            return response;

        }catch (IOException | ParseException e){
            e.printStackTrace();
            return TokenResponseDto.builder().build();
        }
    }

    /**
     * 받은 토큰을 이용하여 사용자 정보를 구글 서버로부터 불러오기
     */
    public GoogleUserInfoDto getUserInfo(TokenResponseDto tokenResponse) throws IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        String idToken = tokenResponse.getId_token();
        String requestUrl = UriComponentsBuilder.fromHttpUrl(GOOGLE_TOKEN_REQUEST_URL + "/tokeninfo").queryParam("id_token", idToken).toUriString();
        try{
            String resultJson = restTemplate.getForObject(requestUrl, String.class);

            if(resultJson != null){
                GoogleUserInfoDto userInfoDto = objectMapper.readValue(resultJson, new TypeReference<GoogleUserInfoDto>() {});
                return userInfoDto;
            }
            else{
                throw new Exception("Google OAuth failed!");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return new GoogleUserInfoDto();
    }

    /**
     * 회원 등록이 안 되어 있을 경우 회원가입
     */
    public void googleSignup(GoogleUserInfoDto userInfo) {
        String socialId = userInfo.getSub();

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
    }
}
