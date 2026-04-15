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
@Table(name = "rbac_permission")
public class RbacPermission {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 64)
  private String code;

  @Column(nullable = false, length = 64)
  private String name;

  @Column(length = 255)
  private String description;

  @Column(nullable = false)
  @Builder.Default
  private Boolean builtin = Boolean.FALSE;
}
