package com.mzoffissu.termterm.controller.auth;

import com.mzoffissu.termterm.exception.AuthorityExceptionType;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
class AuthControllerTest {
    @LocalServerPort
    private int port;

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
        String host = "http://localhost:";
        String path = "/auth/something";
        String url = host + port + path;
        WrongSocialTypeRequestDto requestDto = WrongSocialTypeRequestDto.builder()
                .code("adsd")
                .build();
        HttpEntity<WrongSocialTypeRequestDto> requestEntity = new HttpEntity<>(requestDto);

        //when
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        String responseBody = responseEntity.getBody();
        JSONObject responseJson = (JSONObject) parser.parse(responseBody);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseJson.get("message")).isEqualTo(AuthorityExceptionType.INVALID_SOCIAL_TYPE.getMessage());
    }

    @Test
    @DisplayName("request body에 code가 없을 경우")
    public void noCodeInBody() throws Exception{
        //given
        JSONParser parser = new JSONParser();
        String host = "http://localhost:";
        String path = "/auth/kakao";
        String url = host + port + path;
        WrongDataDto requestDto = WrongDataDto.builder()
                .cade("daa")
                .build();
        HttpEntity<WrongDataDto> requestEntity = new HttpEntity<>(requestDto);

        //when
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        String responseBody = responseEntity.getBody();
        JSONObject responseJson = (JSONObject) parser.parse(responseBody);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseJson.get("message")).isEqualTo(AuthorityExceptionType.NO_AUTHORIZATION_CODE.getMessage());
    }


    @Getter
    static class WrongSocialTypeRequestDto{
        private String code;

        @Builder
        public WrongSocialTypeRequestDto(String code){
            this.code = code;
        }

    }

    @Getter
    static class WrongDataDto{
        private String cade;

        @Builder
        public WrongDataDto(String cade){
            this.cade = cade;
        }
    }
}