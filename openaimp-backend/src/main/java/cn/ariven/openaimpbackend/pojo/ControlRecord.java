package cn.ariven.openaimpbackend.pojo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "control_record")
public class ControlRecord {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_cid", nullable = false)
  private Integer userCid;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_cid", referencedColumnName = "cid", insertable = false, updatable = false)
  private User user;

  @Column(nullable = false, length = 32)
  private String position;

  @Column(length = 8)
  private String airport;

  @Column(nullable = false)
  private Integer facilityType;

  @Column(nullable = false)
  private LocalDateTime startedAt;

  @Column(nullable = false)
  private LocalDateTime endedAt;

  @Column(nullable = false)
  private Long durationMinutes;

  @Column(length = 1000)
  private String remarks;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime updatedAt;
}
