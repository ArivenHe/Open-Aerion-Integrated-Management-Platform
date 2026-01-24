package cn.ariven.openaimpbackend.repository;

import cn.ariven.openaimpbackend.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByCallsign(String callsign);
    Optional<User> findByEmail(String email);
    boolean existsByCallsign(String callsign);
    boolean existsByEmail(String email);
}
