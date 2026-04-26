package cn.ariven.openaimpbackend.service;

import cn.ariven.openaimpbackend.dto.request.RequestUpsertControlRecord;
import cn.ariven.openaimpbackend.dto.response.ResponseControlRecord;
import java.util.List;

public interface ControlRecordService {
  List<ResponseControlRecord> listByUserCid(Integer userCid);

  ResponseControlRecord getById(Long id);

  ResponseControlRecord create(
      Integer userCid, RequestUpsertControlRecord requestUpsertControlRecord);

  ResponseControlRecord update(Long id, RequestUpsertControlRecord requestUpsertControlRecord);

  void delete(Long id);
}
