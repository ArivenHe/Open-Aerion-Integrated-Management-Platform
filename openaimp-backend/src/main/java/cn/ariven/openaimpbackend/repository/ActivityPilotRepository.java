package cn.ariven.openaimpbackend.repository;

import cn.ariven.openaimpbackend.entity.ActivityPilot;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityPilotRepository extends CrudRepository<ActivityPilot, Integer> {
    @Query("select a from ActivityPilot a WHERE a.activity_id=:id AND a.user_cid=:user_id")
    ActivityPilot findPilotByActivityId(int id, int user_id);

    @Modifying
    @Transactional
    @Query("delete from ActivityPilot a where a.user_cid=:cid AND a.activity_id=:id")
    int deletePilotByUserId(int cid, int id);

    @Query("SELECT a FROM ActivityPilot a WHERE a.activity_id = :id")
    List<ActivityPilot> findPilotsByActivityId(int id);
}
