package cn.ariven.openaimpbackend.repository;

import cn.ariven.openaimpbackend.entity.PilotHistory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PilotHistoryRepository extends CrudRepository<PilotHistory, Integer> {

    @Query("SELECT p.cid, u.callsign, SUM(p.onlinetime) as totalOnlineTime " +
            "FROM PilotHistory p " +
            "JOIN User u ON p.cid = u.cid " +
            "GROUP BY p.cid, u.callsign " +
            "ORDER BY totalOnlineTime DESC " +
            "LIMIT 10")
    List<Object[]> findTop10PilotsByTotalOnlineTimeWithCallsign();
}
