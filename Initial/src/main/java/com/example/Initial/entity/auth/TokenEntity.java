package com.example.Initial.entity.auth;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "token")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class TokenEntity {
    @Column(name = "user_id", nullable = false)
    long userId;
    @Column(name = "refresh_token", nullable = false)
    String refreshToken;
    @Column(name = "expire_date", nullable = false)
    LocalDateTime expireDate;
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
