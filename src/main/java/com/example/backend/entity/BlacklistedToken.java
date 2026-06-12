package com.example.backend.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "blacklisted_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlacklistedToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 500, unique = true)
    private String token;

    @Column(name = "expiry_time", nullable = false)
    private LocalDateTime expiryTime;
}
