package cn.ariven.openaimpbackend.repository;

import cn.ariven.openaimpbackend.pojo.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Permission findByCode(String code);
}
