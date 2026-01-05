package cn.ariven.openaimpbackend.service;

import cn.ariven.openaimpbackend.dto.PilotRankingDTO;
import java.util.List;

public interface IPilotRankingService {
    List<PilotRankingDTO> getTop10PilotsByTotalOnlineTime();
}
