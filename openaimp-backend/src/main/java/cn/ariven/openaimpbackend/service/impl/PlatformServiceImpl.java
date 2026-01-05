package cn.ariven.openaimpbackend.service.impl;

import cn.ariven.openaimpbackend.component.AppConfig;
import cn.ariven.openaimpbackend.dto.PlatformStatsDTO;
import cn.ariven.openaimpbackend.dto.ResponseMessage;
import cn.ariven.openaimpbackend.repository.ActivityInfoRepository;
import cn.ariven.openaimpbackend.repository.PlatformRepository;
import cn.ariven.openaimpbackend.service.IPlatformService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlatformServiceImpl implements IPlatformService {

    private final PlatformRepository platformRepository;
    private final ActivityInfoRepository activityInfoRepository;
    private final RestTemplate restTemplate;

    @Override
    public ResponseMessage<PlatformStatsDTO> platformInfo() {
        int userCount = platformRepository.getPlatformUserCount();
        int atcCount = platformRepository.getPlatformAtc();
        int activityCount = activityInfoRepository.getPlatformActivityCount();

        PlatformStatsDTO stats = new PlatformStatsDTO(userCount, atcCount, activityCount);
        return ResponseMessage.success(stats);
    }

    @Override
    public ResponseMessage<Map<String, Object>> getWhazzup() {
        try {
            String vatsimDataUrl = AppConfig.getVatsimDataUrl();
            ResponseEntity<Map> response = restTemplate.getForEntity(vatsimDataUrl, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
                log.info("Successfully fetched VATSIM data");
                return ResponseMessage.success(response.getBody());
            } else {
                log.error("VATSIM API returned non-success status: {}", response.getStatusCode());
                return ResponseMessage.error("Failed to fetch VATSIM data");
            }
        } catch (ResourceAccessException e) {
            log.error("Error accessing VATSIM API: {}", e.getMessage());
            return ResponseMessage.error("VATSIM API is currently unavailable");
        } catch (RestClientException e) {
            log.error("Error while calling VATSIM API: {}", e.getMessage());
            return ResponseMessage.error("Error communicating with VATSIM API");
        }
    }
}
