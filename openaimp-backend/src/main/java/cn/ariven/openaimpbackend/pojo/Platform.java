package cn.ariven.openaimpbackend.pojo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "platform_config")
public class Platform {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String platformName;
  private String platformDescription;
  private String platformLogo;
  private String platformUrl;
  private String platformSignedUserCount;
  private String platformCreateTime;
}
