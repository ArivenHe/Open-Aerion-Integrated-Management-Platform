package cn.ariven.openaimpbackend.mapper;

import cn.ariven.openaimpbackend.pojo.RbacRolePermission;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RbacRolePermissionMapper extends JpaRepository<RbacRolePermission, Long> {
  boolean existsByRoleIdAndPermissionId(Long roleId, Long permissionId);

  boolean existsByPermissionId(Long permissionId);

  void deleteAllByRoleIdAndPermissionId(Long roleId, Long permissionId);

  void deleteAllByRoleId(Long roleId);

  void deleteAllByPermissionId(Long permissionId);

  List<RbacRolePermission> findAllByRoleId(Long roleId);

  List<RbacRolePermission> findAllByRoleIdIn(Collection<Long> roleIds);
}
