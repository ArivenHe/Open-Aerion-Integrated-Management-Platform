package cn.ariven.openaimpbackend.service;

import cn.ariven.openaimpbackend.dto.request.RequestUpsertFlightRecord;
import cn.ariven.openaimpbackend.dto.response.ResponseFlightRecord;
import java.util.List;

public interface FlightRecordService {
  List<ResponseFlightRecord> listByUserCid(Integer userCid);

  ResponseFlightRecord getById(Long id);

  ResponseFlightRecord create(Integer userCid, RequestUpsertFlightRecord requestUpsertFlightRecord);

  ResponseFlightRecord update(Long id, RequestUpsertFlightRecord requestUpsertFlightRecord);

  void delete(Long id);
}
