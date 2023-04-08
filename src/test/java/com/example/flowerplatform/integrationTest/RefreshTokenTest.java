package com.example.flowerplatform.integrationTest;

import com.example.flowerplatform.controller.dto.request.TokenRefreshDto;
import com.example.flowerplatform.dto.MessageFormat;
import com.example.flowerplatform.security.authentication.dto.LoginRequestDto;
import com.example.flowerplatform.security.authentication.userDetails.Role;
import com.example.flowerplatform.security.securityConfig.SecurityConfig;
import com.example.flowerplatform.service.UserService;
import com.example.flowerplatform.service.dto.input.SaveInternalUserServiceDto;
import com.example.flowerplatform.service.exceptions.DuplicateUserException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@EnabledIfEnvironmentVariable(named = "spring.profiles.active", matches = "test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RefreshTokenTest {

    @Autowired
    private MockMvc mvc;

    String accessToken;

    String refreshToken;

    Long userId;

    String email = "harryjung123@gmail.com";
    String password = "asdf1234";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @BeforeAll
    public void saveAndLogIn() throws Exception{
        Role role = Role.USER;

        SaveInternalUserServiceDto saveInternalUserServiceDto = SaveInternalUserServiceDto.builder()
                .email(email)
                .rawPassword(password)
                .role(role)
                .build();
        try {
            userService.saveInternalUser(saveInternalUserServiceDto);
        }
        catch (DuplicateUserException duplicateUserException)
        {
            log.info("user " + email + " already exists!");
        }


        LoginRequestDto loginRequestDto = new LoginRequestDto(email, password);

        ResultActions resultActions = mvc.perform(post(SecurityConfig.LOGIN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        MockHttpServletResponse r = resultActions.andReturn().getResponse();

        MessageFormat msg = objectMapper.readValue(r.getContentAsString(), MessageFormat.class);

        log.debug("============================================ logIn result: " + msg);

        Map<String, Object> tokens = (Map<String, Object>)msg.getData();
        this.accessToken = tokens.get("access_token").toString();
        this.refreshToken = tokens.get("refresh_token").toString();
        this.userId = Long.valueOf(tokens.get("user_id").toString());

        log.debug("=========================================== access token: " + accessToken);
        log.debug("=========================================== refresh token: " + refreshToken);
        log.debug("=========================================== user id: " + String.valueOf(userId));


    }

    @Test
    @Rollback(value = true)
    public void refreshTokens() throws Exception
    {
        //given
        TokenRefreshDto tokenRefreshDto = new TokenRefreshDto(refreshToken,userId);

        //when
        ResultActions resultActions = mvc.perform(post("/users/tokens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tokenRefreshDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());

        MessageFormat msg = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), MessageFormat.class);

        log.debug("============================================ refresh token result: " + msg);

        Map<String, Object> tokens = (Map<String, Object>)msg.getData();

        assertEquals(userId, Long.valueOf(tokens.get("user_id").toString()));
        assertNotEquals(accessToken, tokens.get("access_token").toString());
        assertNotEquals(refreshToken, tokens.get("refresh_token").toString());

    }
}
