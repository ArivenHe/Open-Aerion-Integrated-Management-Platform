package cn.ariven.openaimpbackend.mapper;

import cn.ariven.openaimpbackend.pojo.RbacPermission;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RbacPermissionMapper extends JpaRepository<RbacPermission, Long> {
  boolean existsByCode(String code);

  Optional<RbacPermission> findByCode(String code);

  List<RbacPermission> findByCodeIn(Collection<String> codes);

  List<RbacPermission> findAllByOrderByIdAsc();
}
