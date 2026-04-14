package cn.ariven.openaimpbackend.mapper;

import cn.ariven.openaimpbackend.pojo.Auth;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthMapper extends CrudRepository<Auth, Integer> {
  boolean existsByEmail(String email);

  Auth findAuthByEmail(String email);
}
