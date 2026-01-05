package cn.ariven.openaimpbackend.repository;

import cn.ariven.openaimpbackend.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlatformRepository extends CrudRepository<User, Integer> {
    @Query("SELECT COUNT(u) FROM User u")
    int getPlatformUserCount();

    @Query("SELECT COUNT(u) FROM User u WHERE u.rating>1")
    int getPlatformAtc();
}
