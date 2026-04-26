package cn.ariven.openaimpbackend.mapper;

import cn.ariven.openaimpbackend.pojo.FlightRecord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightRecordMapper extends JpaRepository<FlightRecord, Long> {
  List<FlightRecord> findAllByUserCidOrderByStartedAtDescIdDesc(Integer userCid);

  long countByUserCid(Integer userCid);

  @Query("select coalesce(sum(fr.durationMinutes), 0) from FlightRecord fr where fr.userCid = :userCid")
  Long sumDurationMinutesByUserCid(@Param("userCid") Integer userCid);
}
