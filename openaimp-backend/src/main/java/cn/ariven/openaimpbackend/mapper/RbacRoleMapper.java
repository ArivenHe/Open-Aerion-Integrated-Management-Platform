package cn.ariven.openaimpbackend.mapper;

import cn.ariven.openaimpbackend.pojo.RbacRole;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RbacRoleMapper extends JpaRepository<RbacRole, Long> {
  boolean existsByCode(String code);

  Optional<RbacRole> findByCode(String code);

  List<RbacRole> findByCodeIn(Collection<String> codes);

  List<RbacRole> findAllByOrderByIdAsc();
}
