package cn.ariven.openaimpbackend.service;

import cn.ariven.openaimpbackend.dto.PlatformStatsDTO;
import cn.ariven.openaimpbackend.dto.ResponseMessage;

import java.util.Map;

public interface IPlatformService {
    ResponseMessage<PlatformStatsDTO> platformInfo();
    ResponseMessage<Map<String, Object>> getWhazzup();
}
