package cn.ariven.openaimpbackend.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.ariven.openaimpbackend.dto.PilotRankingDTO;
import cn.ariven.openaimpbackend.dto.PlatformStatsDTO;
import cn.ariven.openaimpbackend.dto.ResponseMessage;
import cn.ariven.openaimpbackend.service.IPilotRankingService;
import cn.ariven.openaimpbackend.service.IPlatformService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@SaCheckRole("user-normal")
@RestController
@RequestMapping("/Platform")
@RequiredArgsConstructor
public class PlatformController {

    private final IPlatformService platformService;
    private final IPilotRankingService pilotRankingService;

    @GetMapping("/PlatformInfo")
    public ResponseMessage<PlatformStatsDTO> platformInfo() {
        return platformService.platformInfo();
    }

    @GetMapping("/whazzup")
    public ResponseMessage<Map<String, Object>> getVatsimData() {
        return platformService.getWhazzup();
    }

    @GetMapping("/PilotRanking")
    public ResponseMessage<List<PilotRankingDTO>> getPilotRanking() {
        List<PilotRankingDTO> ranking = pilotRankingService.getTop10PilotsByTotalOnlineTime();
        return ResponseMessage.success(ranking);
    }
}
