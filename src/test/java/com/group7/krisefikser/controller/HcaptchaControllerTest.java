package com.group7.krisefikser.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group7.krisefikser.dto.request.HcaptchaRequest;
import com.group7.krisefikser.dto.response.HcaptchaVerificationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
@ActiveProfiles("test")
public class HcaptchaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RestTemplate restTemplate; // Injected into controller (you'll need to move RestTemplate to a @Bean)

    @Value("${hcaptcha.secret}")
    private String hcaptchaSecret;

    private MockRestServiceServer server;

    @BeforeEach
    public void setup() {
        server = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void testSuccessfulVerificationWithTestKey() throws Exception {
        HcaptchaRequest request = new HcaptchaRequest();
        request.setToken("10000000-aaaa-bbbb-cccc-000000000001"); // test token

        mockMvc.perform(post("/api/hcaptcha/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testFailedVerification() throws Exception {
        HcaptchaVerificationResponse mockResponse = new HcaptchaVerificationResponse();
        mockResponse.setSuccess(false);

        String responseJson = objectMapper.writeValueAsString(mockResponse);

        server.expect(requestTo("https://hcaptcha.com/siteverify"))
                .andExpect(method(org.springframework.http.HttpMethod.POST))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        HcaptchaRequest request = new HcaptchaRequest();
        request.setToken("bad-token");

        mockMvc.perform(post("/api/hcaptcha/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }
}
