package cn.ariven.openaimpbackend.pojo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "auth")
public class Auth {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer cid;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(updatable = false)
  private LocalDateTime registeredAt;

  private LocalDateTime lastLoginAt;
}
