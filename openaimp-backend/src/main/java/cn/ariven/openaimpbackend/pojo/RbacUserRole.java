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
    name = "rbac_user_role",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role_id"}))
public class RbacUserRole {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Integer userId;

  @Column(name = "role_id", nullable = false)
  private Long roleId;
}
