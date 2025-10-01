package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "otp"
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "email", length = 50)
    private String email;

    @Column(length = 6)
    private String otp;

    @Column(name = "is_used", nullable = false)
    private Boolean isUsed = false;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "temp_token", length = 255)
    private String tempToken;

    @Column(name = "token_expired_at")
    private LocalDateTime tokenExpiredAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

}