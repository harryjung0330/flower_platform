package com.example.flowerplatform.repository.entity.AppUser;

import com.example.flowerplatform.security.authentication.userDetails.Role;
import jakarta.persistence.*;
import lombok.*;


@Entity
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter
@ToString
@Table(name="users")
@Builder
@EqualsAndHashCode
public class AppUser
{
    @Id
    @Column(name="user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email")
    private final String email;

    @Column(name = "password")
    private final String password;

    @Column(name = "role")
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private final Role role = Role.REGISTRATION_NOT_COMPLETE_USER;

    @Column(name="authentication_provider")
    @Enumerated(EnumType.STRING)
    private final AuthenticationProvider authenticationProvider;

    @Column(name = "registration_id")
    private final String registrationId;

}
