package cn.ariven.openaimpbackend.service;

import cn.ariven.openaimpbackend.dto.request.RequestUpsertAtc;
import cn.ariven.openaimpbackend.dto.response.ResponseAtcBinding;
import java.util.List;

public interface AtcService {
  List<ResponseAtcBinding> listBindings();

  ResponseAtcBinding getBinding(Integer userId);

  ResponseAtcBinding upsertBinding(RequestUpsertAtc requestUpsertAtc);

  void deleteBinding(Integer userId);
}
