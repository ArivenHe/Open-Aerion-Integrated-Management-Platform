package cn.ariven.openaimpbackend.repository;

import cn.ariven.openaimpbackend.entity.ActivitySeatInfo;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivitySeatInfoRepository extends CrudRepository<ActivitySeatInfo, Integer> {
    @Query("SELECT a FROM ActivitySeatInfo a WHERE a.activityId = :id")
    List<ActivitySeatInfo> findActivitySeatInfoByActivityId(int id);

    @Modifying
    @Transactional
    @Query("update ActivitySeatInfo a set a.userCid=null , a.userCallsign=null where a.activityId=:id AND a.userCid=:cid")
    int deleteAtcByUserIdAndActivityId(int cid, int id);
}
