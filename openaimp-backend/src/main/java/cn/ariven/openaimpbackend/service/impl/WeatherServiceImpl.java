package cn.ariven.openaimpbackend.service.impl;

import cn.ariven.openaimpbackend.dto.ResponseMessage;
import cn.ariven.openaimpbackend.dto.RouteSearchDTO;
import cn.ariven.openaimpbackend.service.IWeatherService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherServiceImpl implements IWeatherService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public ResponseMessage<Object> getAllWeather(String icao) {
        try {
            String metarRaw = getMetarByIcao(icao);
            String tafRaw = getTafByIcao(icao);

            var metarMap = objectMapper.readValue(metarRaw, java.util.Map.class);
            var tafMap = objectMapper.readValue(tafRaw, java.util.Map.class);

            var metarData = (java.util.Map<String, Object>) metarMap.get("data");
            var tafData = (java.util.Map<String, Object>) tafMap.get("data");
            String metarDecodeStr = (String) metarData.get("metarDecode");
            if (metarDecodeStr != null && !metarDecodeStr.isEmpty()) {
                var metarDecodeObj = objectMapper.readValue(metarDecodeStr, java.util.Map.class);
                metarData.put("Decode", metarDecodeObj);
            }
            java.util.Map<String, Object> merged = new java.util.HashMap<>();

            if (metarData != null) {
                for (var entry : metarData.entrySet()) {
                    if ("icao".equals(entry.getKey())) {
                        merged.put("icao_metar", entry.getValue());
                    } else {
                        merged.put(entry.getKey(), entry.getValue());
                    }
                }
            }

            if (tafData != null) {
                for (var entry : tafData.entrySet()) {
                    if ("icao".equals(entry.getKey())) {
                        merged.put("icao_taf", entry.getValue());
                    } else {
                        merged.put(entry.getKey(), entry.getValue());
                    }
                }
            }

            return ResponseMessage.success(merged);
        } catch (Exception e) {
            log.error("Error getting weather for {}: {}", icao, e.getMessage());
            return ResponseMessage.error("查询天气失败: " + e.getMessage());
        }
    }

    @Override
    public ResponseMessage<Object> getAirportInfo(String icao) {
        String url = "https://api.xflysim.com/pilot/api/realTimeMap/airports/" + icao;
        Object data = restTemplate.getForObject(url, Object.class);
        return ResponseMessage.success(data);
    }

    @Override
    public ResponseMessage<Object> getAirportAtis(String icao) {
        String url = "https://api.xflysim.com/pilot/api/realTimeMap/atis/" + icao;
        Object data = restTemplate.getForObject(url, Object.class);
        return ResponseMessage.success(data);
    }

    @Override
    public ResponseMessage<Object> getRoute(RouteSearchDTO routeSearchDTO) {
        try {
            Object cycleObj = getRouteCycle();
            log.info("cycle:{}", cycleObj);

            Map<String, Object> map = objectMapper.convertValue(cycleObj, new TypeReference<Map<String, Object>>() {});
            String dataStr = (String) map.get("data");
            List<String> list = objectMapper.readValue(dataStr, new TypeReference<List<String>>() {});

            int max = list.stream()
                    .mapToInt(Integer::parseInt)
                    .max()
                    .orElseThrow();

            log.info("最大值: {}", max);

            routeSearchDTO.setCycle(String.valueOf(max));
            
            Object data = fetchRoute(routeSearchDTO.getDep(), routeSearchDTO.getArr(), routeSearchDTO.getCycle());
            return ResponseMessage.success(data);
        } catch (Exception e) {
            log.error("Error getting route: {}", e.getMessage());
            return ResponseMessage.error("查询航路失败: " + e.getMessage());
        }
    }

    private String getMetarByIcao(String icao) {
        String url = "https://api.xflysim.com/pilot/api/realTimeMap/weather/" + icao;
        return restTemplate.getForObject(url, String.class);
    }

    private String getTafByIcao(String icao) {
        String url = "https://api.xflysim.com/pilot/api/realTimeMap/weatherForecast/" + icao;
        return restTemplate.getForObject(url, String.class);
    }

    private Object getRouteCycle() {
        String url = "https://api.xflysim.com/pilot/api/realTimeMap/getRouteCycle";
        return restTemplate.getForObject(url, Object.class);
    }

    private Object fetchRoute(String dep, String arr, String cycle) {
        String url = "https://api.xflysim.com/pilot/api/realTimeMap/route?dep=" + dep + "&arr=" + arr + "&cycle=" + cycle;

        Map<String, String> request = new HashMap<>();
        request.put("dep", dep);
        request.put("arr", arr);
        request.put("cycle", cycle);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

        Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);

        if (response != null && response.containsKey("data")) {
            try {
                Object data = response.get("data");
                if (data instanceof String) {
                    Object json = objectMapper.readValue((String) data, Object.class);
                    response.put("data", json);
                }
            } catch (Exception e) {
                log.error("Error parsing route response data: {}", e.getMessage());
            }
        }
        return response;
    }
}
