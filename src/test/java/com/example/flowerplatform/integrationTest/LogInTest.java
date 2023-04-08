package com.example.flowerplatform.integrationTest;

import com.example.flowerplatform.dto.MessageFormat;
import com.example.flowerplatform.security.authentication.dto.LoginRequestDto;
import com.example.flowerplatform.security.authentication.userDetails.Role;
import com.example.flowerplatform.security.securityConfig.SecurityConfig;
import com.example.flowerplatform.service.UserService;
import com.example.flowerplatform.service.dto.input.SaveInternalUserServiceDto;
import com.example.flowerplatform.service.exceptions.DuplicateUserException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@EnabledIfEnvironmentVariable(named = "spring.profiles.active", matches = "test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)  //lifecycle이 각 method 단위로 설정 안하고, 클래스 단위로 설정
public class LogInTest
{
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    String email = "harryjung123@gmail.com";
    String password = "asdf1234";

    @BeforeAll
    public void beforeLogIn(){

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
    }


    @Test
    @Rollback(value = true)
    @Transactional
    public void testLogIn() throws Exception
    {
        //given

        LoginRequestDto loginRequestDto = new LoginRequestDto(email, password);
        //when
        ResultActions resultActions = mvc.perform(post(SecurityConfig.LOGIN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());


        MockHttpServletResponse r = resultActions.andReturn().getResponse();
        MessageFormat msg = objectMapper.readValue(r.getContentAsString(), MessageFormat.class);


        log.info("============================================ logIn result: " + msg);

        log.info("============================================ data class type: " + msg.getData().getClass());

        String accessToken = ((Map<String, String>)msg.getData()).get("access_token");
        log.info("=========================================== token: " + accessToken);

    }

    @Test
    public void testFailedLogIn() throws Exception{
        //given
        String wrongEmail = "wrongEmail@gmail.com";

        LoginRequestDto loginRequestDto = new LoginRequestDto(wrongEmail, password);
        //when
        ResultActions resultActions = mvc.perform(post(SecurityConfig.LOGIN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        //then
        resultActions
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$").isNotEmpty());
    }



}
