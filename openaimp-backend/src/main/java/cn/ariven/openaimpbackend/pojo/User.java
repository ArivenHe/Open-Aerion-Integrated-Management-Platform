package cn.ariven.openaimpbackend.pojo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_profile")
public class User {
  @Id
  @Column(nullable = false)
  private Integer cid;

  @Column(nullable = false, length = 100)
  private String nickname;

  @Column(length = 100)
  private String firstName;

  @Column(length = 100)
  private String lastName;

  @Column(length = 255)
  private String avatar;

  @Column(length = 1000)
  private String bio;

  @Column(nullable = false)
  @Builder.Default
  private Integer flightRecordCount = 0;

  @Column(nullable = false)
  @Builder.Default
  private Long flightDurationMinutes = 0L;

  @Column(nullable = false)
  @Builder.Default
  private Integer controlRecordCount = 0;

  @Column(nullable = false)
  @Builder.Default
  private Long controlDurationMinutes = 0L;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime updatedAt;
}
