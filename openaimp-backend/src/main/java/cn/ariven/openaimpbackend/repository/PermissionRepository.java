package cn.ariven.openaimpbackend.repository;

import cn.ariven.openaimpbackend.entity.Permission;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends CrudRepository<Permission, Integer> {
}
