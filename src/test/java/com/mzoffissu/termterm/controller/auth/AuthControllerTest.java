package com.mzoffissu.termterm.controller.auth;

import com.mzoffissu.termterm.response.exception.AuthorityExceptionType;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
class AuthControllerTest {
    @Value("${server.host}")
    private String HOST;

    @LocalServerPort
    private int PORT;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    public void setup(){
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Test
    @DisplayName("소셜타입 경로가 틀려 NullPointerException이 발생한다.")
    public void wrongSocialType() throws Exception{
        //given
        JSONParser parser = new JSONParser();
        String path = "/auth/something";
        String url = HOST + PORT + path;

        HttpHeaders headers = new HttpHeaders();
        headers.set("auth-code", "assasd");
        HttpEntity requestEntity = new HttpEntity(headers);

        //when
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        String responseBody = responseEntity.getBody();
        JSONObject responseJson = (JSONObject) parser.parse(responseBody);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseJson.get("message")).isEqualTo(AuthorityExceptionType.INVALID_SOCIAL_TYPE.getMessage());
    }

    @Test
    @DisplayName("request header에 code가 없을 경우")
    public void noCodeInHeader() throws Exception{
        //given
        JSONParser parser = new JSONParser();
        String path = "/auth/kakao";
        String url = HOST + PORT + path;
//        WrongDataDto requestDto = WrongDataDto.builder()
//                .cade("daa")
//                .build();
//        HttpEntity<WrongDataDto> requestEntity = new HttpEntity<>(requestDto);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity requestEntity = new HttpEntity(headers);

        //when
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        String responseBody = responseEntity.getBody();
        JSONObject responseJson = (JSONObject) parser.parse(responseBody);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseJson.get("message")).isEqualTo(AuthorityExceptionType.NO_AUTHORIZATION_CODE.getMessage());
    }


    @Test
    @DisplayName("code가 잘못 왔을 경우 504")
    public void invalidCode() throws Exception{
        //given
        JSONParser parser = new JSONParser();
        String path = "/auth/kakao";
        String url = HOST + PORT + path;

        HttpHeaders headers = new HttpHeaders();
        headers.set("auth-code", "das");
        HttpEntity requestEntity = new HttpEntity(headers);

        //when
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        String responseBody = responseEntity.getBody();
        JSONObject responseJson = (JSONObject) parser.parse(responseBody);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.GATEWAY_TIMEOUT);
        assertThat(responseJson.get("message")).isEqualTo(AuthorityExceptionType.KAKAO_CONNECTION_ERROR.getMessage());
    }
}