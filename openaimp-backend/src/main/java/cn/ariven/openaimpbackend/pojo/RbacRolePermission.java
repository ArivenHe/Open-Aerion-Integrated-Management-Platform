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
    name = "rbac_role_permission",
    uniqueConstraints = @UniqueConstraint(columnNames = {"role_id", "permission_id"}))
public class RbacRolePermission {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "role_id", nullable = false)
  private Long roleId;

  @Column(name = "permission_id", nullable = false)
  private Long permissionId;
}
