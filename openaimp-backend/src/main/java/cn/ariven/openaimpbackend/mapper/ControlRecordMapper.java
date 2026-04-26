package cn.ariven.openaimpbackend.mapper;

import cn.ariven.openaimpbackend.pojo.ControlRecord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ControlRecordMapper extends JpaRepository<ControlRecord, Long> {
  List<ControlRecord> findAllByUserCidOrderByStartedAtDescIdDesc(Integer userCid);

  long countByUserCid(Integer userCid);

  @Query("select coalesce(sum(cr.durationMinutes), 0) from ControlRecord cr where cr.userCid = :userCid")
  Long sumDurationMinutesByUserCid(@Param("userCid") Integer userCid);
}
