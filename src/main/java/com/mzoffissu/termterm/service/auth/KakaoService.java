package com.mzoffissu.termterm.service.auth;

import com.mzoffissu.termterm.domain.auth.Role;
import com.mzoffissu.termterm.domain.auth.SocialLoginType;
import com.mzoffissu.termterm.domain.auth.User;
import com.mzoffissu.termterm.dto.auth.TokenResponseDto;
import com.mzoffissu.termterm.dto.auth.KakaoUserInfoDto;
import com.mzoffissu.termterm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoService {
    private static final Integer CONN_TIMEOUT = 15 * 1000;  // 15초
    private static final String KAKAO_TOKEN_REQUEST_URL = "https://kauth.kakao.com/oauth/token";
    private static final String KAKAO_USERINFO_REQUEST_URL = "https://kapi.kakao.com/v2/user/me";

    @Value("${auth.kakao.client-id}")
    private String CLIENT_ID;

    @Value("${auth.kakao.redirect-uri}")
    private String REDIRECT_URI;

    private final UserRepository userRepository;

    /**
     * 인가 코드로 토큰 받기
     */
    public String getToken(String authorizationCode) throws IOException {
        URL url = new URL(KAKAO_TOKEN_REQUEST_URL);
        String token = "";

        try{
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
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
            urlConnection.getResponseCode();

            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line = "";
            String result = "";
            while ((line = br.readLine()) != null) {
                result += line;
            }

            // json parsing
            JSONParser parser = new JSONParser();
            JSONObject elem = (JSONObject) parser.parse(result);

            TokenResponseDto response = TokenResponseDto.builder()
                    .token_type(elem.get("token_type").toString())
                    .access_token(elem.get("access_token").toString())
                    .expires_in(elem.get("expires_in").toString())
                    .refresh_token(elem.get("refresh_token").toString())
                    .refresh_token_expires_in(elem.get("refresh_token_expires_in").toString())
                    .scope(elem.get("scope").toString())
                    .build();

            token = response.getAccess_token();

        }catch (IOException | ParseException e){
            e.printStackTrace();
            return "";
        }

        return token;
    }

    /**
     * 받은 토큰을 이용하여 사용자 정보를 카카오 서버로부터 불러오기
     *
     * name 권한 획득 시 .name(name)으로 바꿀 것
     */
    public KakaoUserInfoDto getUserInfo(String accessToken) throws IOException{
        URL url = new URL(KAKAO_USERINFO_REQUEST_URL);
        KakaoUserInfoDto userInfo;

        try{
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

            urlConnection.getResponseCode();

            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line = "";
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

            userInfo = KakaoUserInfoDto.builder()
                    .socialId(socialId)
                    .name(nickname)     // name 권한 획득 시 .name(name)으로 바꿀 것
                    .email(email)
                    .nickname(nickname)
                    .isDefaultImage(is_default_image)
                    .thumbnailImageUrl(thumbnail_image_url)
                    .build();

        }catch (IOException | ParseException e){
            e.printStackTrace();
            return KakaoUserInfoDto.builder().build();
        }
        return userInfo;
    }

    /**
     * 회원 등록이 안 되어 있을 경우 회원가입
     */
    public void kakaoSignup(KakaoUserInfoDto userInfo) {
        String socialId = userInfo.getSocialId();

        Boolean isRegistered = !userRepository.findBySocialId(socialId).equals(Optional.empty());
        if (isRegistered) {
            return;
        }

//        String name = userInfo.getName();
        String email = userInfo.getEmail();
        String nickname = userInfo.getNickname();
        String thumbnailImageUrl = userInfo.getThumbnailImageUrl();

        User user = User.builder()
                .socialId(socialId)
                .name(nickname)
                .email(email)
                .picture(thumbnailImageUrl)
                .role(Role.USER)
                .socialLoginType(SocialLoginType.KAKAO)
                .build();
        userRepository.save(user);
        log.info("회원가입 : {} ({})", user.getEmail(), user.getName());
    }
}
