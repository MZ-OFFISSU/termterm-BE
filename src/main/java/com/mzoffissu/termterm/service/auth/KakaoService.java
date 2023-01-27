package com.mzoffissu.termterm.service.auth;

import com.mzoffissu.termterm.domain.auth.Role;
import com.mzoffissu.termterm.domain.auth.User;
import com.mzoffissu.termterm.dto.auth.KakaoGetTokenURLResponseDto;
import com.mzoffissu.termterm.dto.auth.KakaoUserInfoDto;
import com.mzoffissu.termterm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
public class KakaoService {
    private static final Integer CONN_TIMEOUT = 30;  // 30초

    @Value("${auth.kakao.client-id}")
    private String CLIENT_ID;

    @Value("${auth.kakao.redirect-uri}")
    private String REDIRECT_URI;

    private final UserRepository userRepository;

    /**
     * 인가 코드로 토큰 받기
     */
    public String getToken(String authorizationCode) throws IOException {
        // 인가코드로 토큰받기
        String host = "https://kauth.kakao.com/oauth/token";
        URL url = new URL(host);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        String token = "";

        try{
            urlConnection.setRequestMethod("POST");
            urlConnection.setConnectTimeout(CONN_TIMEOUT * 1000);
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

            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line = "";
            String result = "";
            while ((line = br.readLine()) != null) {
                result += line;
            }

            // json parsing
            JSONParser parser = new JSONParser();
            JSONObject elem = (JSONObject) parser.parse(result);

            KakaoGetTokenURLResponseDto response = KakaoGetTokenURLResponseDto.builder()
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
     */
    public KakaoUserInfoDto getUserInfo(String accessToken) throws IOException{
        String host = "https://kapi.kakao.com/v2/user/me";
        KakaoUserInfoDto userInfo;

        try{
            URL url = new URL(host);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

            Integer responseCode = urlConnection.getResponseCode();

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
//                    .name(name)
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
//                .name(name)
                .email(email)
                .nickname(nickname)
                .picture(thumbnailImageUrl)
                .role(Role.USER)
                .build();
        userRepository.save(user);
    }
}
