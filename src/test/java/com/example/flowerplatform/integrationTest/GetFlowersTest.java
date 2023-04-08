package com.example.flowerplatform.integrationTest;

import com.example.flowerplatform.dto.MessageFormat;
import com.example.flowerplatform.security.authentication.dto.LoginRequestDto;
import com.example.flowerplatform.security.authentication.userDetails.Role;
import com.example.flowerplatform.security.securityConfig.SecurityConfig;
import com.example.flowerplatform.service.UserService;
import com.example.flowerplatform.service.dto.input.SaveInternalUserServiceDto;
import com.example.flowerplatform.service.exceptions.DuplicateUserException;
import com.example.flowerplatform.util.tokenManager.implementations.properties.TokenProperties;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;


import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
public class GetFlowersTest
{
    @Autowired
    private MockMvc mvc;

    String accessToken;

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
            log.debug("user " + email + " already exists!");
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

        String accessToken = ((Map<String, String>)msg.getData()).get("access_token");
        log.debug("=========================================== token: " + accessToken);

        this.accessToken = accessToken;


    }

    @Test
    public void testGetFlowersWrongHeaderName() throws Exception{

        //when
        ResultActions resultActions = mvc.perform(get("/flowers")
                        .header("access_token", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        //then
        resultActions
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$").isNotEmpty());

    }

    @Test
    public void testGetFlowersSuccess() throws Exception{

        //when
        ResultActions resultActions = mvc.perform(get("/flowers")
                        .header(TokenProperties.HEADER_ACCESS_KEY, accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());

    }


}
