package cn.ariven.openaimpbackend.repository;

import cn.ariven.openaimpbackend.entity.PilotHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OnlinePilotRepository extends JpaRepository<PilotHistory, Integer> {
    @Query("SELECT a FROM PilotHistory a WHERE a.cid = :cid")
    List<PilotHistory> findPilotHistoriesByCid(int cid);
}
