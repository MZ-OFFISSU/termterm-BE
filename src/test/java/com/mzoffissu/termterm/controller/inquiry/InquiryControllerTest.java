package com.mzoffissu.termterm.controller.inquiry;

import com.mzoffissu.termterm.response.exception.InquiryExceptionType;
import com.mzoffissu.termterm.response.success.InquirySuccessType;
import com.mzoffissu.termterm.vo.ResponseMessage;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@ActiveProfiles("test")
class InquiryControllerTest {
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

    private HttpHeaders headers = new HttpHeaders();
    private JSONParser parser;

    @Test
    @DisplayName("/inquiry/save 성공")
    public void goodInquiry() throws Exception{
        //given
        String saveInquiryUrl = HOST + PORT + "/inquiry/save";
        String email = "email@email.com";
        String content = "content";
        parser = new JSONParser();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", email);
        jsonObject.put("content", content);
        jsonObject.put("type", "OTHER");

        HttpEntity<String> requestEntity = new HttpEntity<String>(jsonObject.toString(), headers);

        //when
        ResponseEntity<String> responseEntity = restTemplate.exchange(saveInquiryUrl, HttpMethod.POST, requestEntity, String.class);
        String responseBody = responseEntity.getBody();
        JSONObject responseJson = (JSONObject) parser.parse(responseBody);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseJson.get("message")).isEqualTo(InquirySuccessType.INQUIRY_ACCEPTED.getMessage());
    }

    @Test
    @DisplayName("inquiry 타입 오타로 인해 오류 발생")
    public void wrongInquiryType() throws Exception{
        //given
        String saveInquiryUrl = HOST + PORT + "/inquiry/save";
        String email = "email@email.com";
        String content = "content";
        parser = new JSONParser();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", email);
        jsonObject.put("content", content);
        jsonObject.put("type", "NOTHING");

        HttpEntity<String> requestEntity = new HttpEntity<String>(jsonObject.toString(), headers);

        //when
        ResponseEntity<String> responseEntity = restTemplate.exchange(saveInquiryUrl, HttpMethod.POST, requestEntity, String.class);
        String responseBody = responseEntity.getBody();
        JSONObject responseJson = (JSONObject) parser.parse(responseBody);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseJson.get("message")).isEqualTo(InquiryExceptionType.INVALID_INQUIRY_TYPE.getMessage());
    }

}