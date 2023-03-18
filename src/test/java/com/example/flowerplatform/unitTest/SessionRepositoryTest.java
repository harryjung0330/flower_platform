package com.example.flowerplatform.unitTest;

import com.example.flowerplatform.repository.SessionRepository;
import com.example.flowerplatform.repository.UserRepository;
import com.example.flowerplatform.repository.entity.AppUser.AppUser;
import com.example.flowerplatform.repository.entity.AppUser.AuthenticationProvider;
import com.example.flowerplatform.repository.entity.Session;
import com.example.flowerplatform.security.authentication.userDetails.Role;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
@ActiveProfiles(profiles = "dev")
@IfProfileValue(name ="spring.profiles.active", value ="dev")
public class SessionRepositoryTest
{
    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    UserRepository userRepository;



    @BeforeEach
    public void refreshRepository(){
        sessionRepository.deleteAll();
        userRepository.deleteAll();
    }

    private AppUser createUser(AppUser appUser){

        if(appUser == null){
            appUser = AppUser.builder()
                    .registrationId("011111123")
                    .authenticationProvider(AuthenticationProvider.GOOGLE)
                    .role(Role.USER)
                    .build();
        }

        return userRepository.save(appUser);
    }

    @Test
    public void saveSessionTest(){
        Calendar calendar = Calendar.getInstance();
        Date createdAt = new Date();

        calendar.setTime(createdAt);
        calendar.add(Calendar.MINUTE, 5);

        Date expiresAt = calendar.getTime();

        AppUser appUser = createUser(null);

        Session session = Session.builder()
                .expiresAt(expiresAt)
                .refreshToken("asdfasfasd.asdfasdf.asdfadfs")
                .userId(appUser.getId())
                .build();

        Session postSave = sessionRepository.save(session);

        assertEquals(session.getExpiresAt(), postSave.getExpiresAt());
        assertEquals(session.getRefreshToken(), postSave.getRefreshToken());
        assertEquals(session.getUserId(), postSave.getUserId());

    }

    @Test
    public void updateSessionTest(){
        Calendar calendar = Calendar.getInstance();
        Date createdAt = new Date();

        calendar.setTime(createdAt);
        calendar.add(Calendar.MINUTE, 5);

        Date expiresAt = calendar.getTime();

        AppUser appUser = createUser(null);

        Session session = Session.builder()
                .userId(appUser.getId())
                .build();

        Session postSave = sessionRepository.save(session);

        assertEquals(session.getExpiresAt(), postSave.getExpiresAt());
        assertEquals(session.getRefreshToken(), postSave.getRefreshToken());
        assertEquals(session.getUserId(), postSave.getUserId());


        Session sessionToUpdate = Session.builder()
                .sessionId(postSave.getSessionId())
                .userId(postSave.getUserId())
                .expiresAt(expiresAt)
                .refreshToken("asfasdfs.aasdfasd.asdfasf")
                .build();

        Session postUpdate = sessionRepository.save(sessionToUpdate);

        assertEquals(sessionToUpdate.getExpiresAt(), postUpdate.getExpiresAt());
        assertEquals(sessionToUpdate,postUpdate);
    }
}
