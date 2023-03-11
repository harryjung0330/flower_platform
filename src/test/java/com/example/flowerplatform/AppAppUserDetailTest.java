package com.example.flowerplatform;

import com.example.flowerplatform.security.authentication.userDetails.AppUserDetails;
import com.example.flowerplatform.security.authentication.userDetails.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AppAppUserDetailTest
{

    @Test
    @DisplayName("test AppUserDetails creation and methods")
    public void testPasswordEncoderIsNotNull()
    {
        String email = "harryjung0330@gmail.com";
        String password = "helloworld!";
        Role role = Role.USER;
        Long userId = Long.valueOf(1);

        UserDetails userDetails = new AppUserDetails(userId, role, email , password);


        assertEquals(userDetails.getUsername(), email );
        assertEquals(userDetails.getPassword(), password);
        assertEquals(userDetails.getAuthorities().stream().toList().get(0), new SimpleGrantedAuthority(role.toString()));
        assertEquals(((AppUserDetails) userDetails).getUserId(), userId);
    }
}
