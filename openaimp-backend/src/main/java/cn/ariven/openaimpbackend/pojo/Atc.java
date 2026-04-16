package cn.ariven.openaimpbackend.pojo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "atc",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id"}))
public class Atc {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Integer userId;

  @Column(name = "network_rating", nullable = false)
  @Builder.Default
  private Integer networkRating = 1;

  @Column(name = "facility_type", nullable = false)
  @Builder.Default
  private Integer facilityType = 0;

  @Column(nullable = false)
  @Builder.Default
  private Boolean enabled = Boolean.TRUE;

  @Column(length = 255)
  private String remarks;
}
