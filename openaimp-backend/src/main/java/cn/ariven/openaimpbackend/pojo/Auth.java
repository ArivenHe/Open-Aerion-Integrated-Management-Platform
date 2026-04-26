package cn.ariven.openaimpbackend.pojo;

import jakarta.persistence.*;
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
