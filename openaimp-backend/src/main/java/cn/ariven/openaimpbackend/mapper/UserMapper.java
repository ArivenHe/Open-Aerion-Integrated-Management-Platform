package cn.ariven.openaimpbackend.mapper;

import cn.ariven.openaimpbackend.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper extends JpaRepository<User, Integer> {}
