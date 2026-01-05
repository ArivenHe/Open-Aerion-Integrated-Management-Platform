package cn.ariven.openaimpbackend.repository;

import cn.ariven.openaimpbackend.entity.UserRole;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends CrudRepository<UserRole, String> {
}
