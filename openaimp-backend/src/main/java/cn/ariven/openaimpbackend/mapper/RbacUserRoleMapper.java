package cn.ariven.openaimpbackend.mapper;

import cn.ariven.openaimpbackend.pojo.RbacUserRole;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RbacUserRoleMapper extends JpaRepository<RbacUserRole, Long> {
  boolean existsByUserIdAndRoleId(Integer userId, Long roleId);

  boolean existsByRoleId(Long roleId);

  void deleteAllByUserIdAndRoleId(Integer userId, Long roleId);

  List<RbacUserRole> findAllByUserId(Integer userId);

  List<RbacUserRole> findAllByUserIdIn(Collection<Integer> userIds);
}
