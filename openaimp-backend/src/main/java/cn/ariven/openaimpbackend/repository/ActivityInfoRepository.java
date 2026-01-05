package cn.ariven.openaimpbackend.repository;

import cn.ariven.openaimpbackend.entity.ActivityInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityInfoRepository extends CrudRepository<ActivityInfo, Integer> {

    @Query("SELECT COUNT(a) FROM ActivityInfo a")
    int getPlatformActivityCount();
}
