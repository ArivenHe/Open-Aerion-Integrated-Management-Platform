package cn.ariven.openaimpbackend.mapper;

import cn.ariven.openaimpbackend.pojo.Auth;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthMapper extends JpaRepository<Auth, Integer> {
  boolean existsByEmail(String email);

  Auth findAuthByEmail(String email);

  List<Auth> findAllByOrderByCidAsc();

  @Modifying
  @Query(
      value = "insert into auth (cid, email, password) values (:cid, :email, :password)",
      nativeQuery = true)
  int insertWithCid(
      @Param("cid") Integer cid,
      @Param("email") String email,
      @Param("password") String password);
}
