package cn.ariven.openaimpbackend.service.impl;

import cn.ariven.openaimpbackend.dto.PilotRankingDTO;
import cn.ariven.openaimpbackend.repository.PilotHistoryRepository;
import cn.ariven.openaimpbackend.service.IPilotRankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PilotRankingServiceImpl implements IPilotRankingService {

    private final PilotHistoryRepository pilotHistoryRepository;

    @Override
    public List<PilotRankingDTO> getTop10PilotsByTotalOnlineTime() {
        List<Object[]> results = pilotHistoryRepository.findTop10PilotsByTotalOnlineTimeWithCallsign();

        return results.stream()
                .map(row -> {
                    long onlineTime;
                    if (row[2] instanceof String) {
                        onlineTime = Long.parseLong((String) row[2]);
                    } else {
                        onlineTime = (long) row[2];
                    }
                    return new PilotRankingDTO(
                            (int) row[0],      // cid
                            (String) row[1],   // callsign
                            onlineTime         // onlineTime
                    );
                })
                .collect(Collectors.toList());
    }
}
