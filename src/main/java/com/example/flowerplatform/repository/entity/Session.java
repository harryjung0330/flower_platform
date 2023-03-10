package com.example.flowerplatform.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter
@ToString
@Table(name="session")
@Builder
@EqualsAndHashCode
public class Session
{
    @Id
    @Column(name="session_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sessionId;

    @Column(name = "expires_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiresAt;

    @Column(name="user_id")
    private Long userId;

    @Column(name="refresh_token")
    private String refreshToken;

}
