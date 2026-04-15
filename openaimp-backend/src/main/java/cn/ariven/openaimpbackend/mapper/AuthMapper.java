package cn.ariven.openaimpbackend.mapper;

import cn.ariven.openaimpbackend.pojo.Auth;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthMapper extends JpaRepository<Auth, Integer> {
  boolean existsByEmail(String email);

  Auth findAuthByEmail(String email);

  List<Auth> findAllByOrderByCidAsc();
}
