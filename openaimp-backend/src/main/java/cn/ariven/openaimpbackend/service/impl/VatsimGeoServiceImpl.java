package cn.ariven.openaimpbackend.service.impl;

import cn.ariven.openaimpbackend.dto.ResponseMessage;
import cn.ariven.openaimpbackend.service.IRegionService;
import cn.ariven.openaimpbackend.service.IVatsimGeoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class VatsimGeoServiceImpl implements IVatsimGeoService {

    private final IRegionService regionService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate redisTemplate;

    @Value("${url.whazzup}")
    private String vatsimDataUrl;

    private static final String REDIS_PILOTS_KEY = "vatsim:pilots:current";
    private static final String REDIS_CONTROLLERS_KEY = "vatsim:controllers:current";

    @Override
    @Scheduled(fixedRate = 5000)
    public Map<String, Object> fetchAndStorePilots() {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(vatsimDataUrl, Map.class);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.error("Failed to fetch VATSIM data");
                return Collections.emptyMap();
            }

            Map<String, Object> body = response.getBody();
            log.info("Fetched VATSIM data successfully");

            // ---- Pilots ----
            Object pilotsObj = body.get("pilots");
            Set<String> currentCids = new HashSet<>();

            if (pilotsObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> pilots = (List<Map<String, Object>>) pilotsObj;
                for (Map<String, Object> pilot : pilots) {
                    try {
                        String cid = String.valueOf(pilot.get("cid"));
                        currentCids.add(cid);
                        String json = objectMapper.writeValueAsString(pilot);
                        redisTemplate.opsForHash().put(REDIS_PILOTS_KEY, cid, json);

                        // Save trail
                        Double lat = getDouble(pilot.get("latitude"));
                        Double lon = getDouble(pilot.get("longitude"));
                        if (lat != null && lon != null) {
                            String trailKey = "vatsim:pilot:" + cid + ":trail";
                            Map<String, Object> point = Map.of(
                                    "lat", lat,
                                    "lon", lon,
                                    "ts", System.currentTimeMillis()
                            );
                            String pointJson = objectMapper.writeValueAsString(point);
                            redisTemplate.opsForList().rightPush(trailKey, pointJson);
                            redisTemplate.opsForList().trim(trailKey, -50, -1);
                        }
                    } catch (JsonProcessingException e) {
                        log.error("Failed to serialize pilot {}: {}", pilot.get("cid"), e.getMessage());
                    }
                }
            }

            // ---- Controllers ----
            Object controllersObj = body.get("controllers");
            if (controllersObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> controllers = (List<Map<String, Object>>) controllersObj;
                for (Map<String, Object> controller : controllers) {
                    try {
                        String cid = String.valueOf(controller.get("cid"));
                        String json = objectMapper.writeValueAsString(controller);
                        redisTemplate.opsForHash().put(REDIS_CONTROLLERS_KEY, cid, json);
                    } catch (JsonProcessingException e) {
                        log.error("Failed to serialize controller {}: {}", controller.get("cid"), e.getMessage());
                    }
                }
            }

            // Cleanup disconnected pilots
            Map<Object, Object> existingPilots = redisTemplate.opsForHash().entries(REDIS_PILOTS_KEY);
            if (existingPilots != null && !existingPilots.isEmpty()) {
                for (Object existingCid : existingPilots.keySet()) {
                    String cidStr = String.valueOf(existingCid);
                    if (!currentCids.contains(cidStr)) {
                        redisTemplate.opsForHash().delete(REDIS_PILOTS_KEY, cidStr);
                        String trailKey = "vatsim:pilot:" + cidStr + ":trail";
                        redisTemplate.delete(trailKey);
                        log.info("Removed disconnected pilot {} from Redis", cidStr);
                    }
                }
            }

            return body;
        } catch (Exception e) {
            log.error("Error in fetchAndStorePilots", e);
            return Collections.emptyMap();
        }
    }

    @Override
    public Map<String, Object> generatePilotsGeoJson(Double swLng, Double swLat, Double neLng, Double neLat) {
        Map<Object, Object> allPilots = redisTemplate.opsForHash().entries(REDIS_PILOTS_KEY);
        List<Map<String, Object>> features = new ArrayList<>();

        if (allPilots != null && !allPilots.isEmpty()) {
            for (Map.Entry<Object, Object> entry : allPilots.entrySet()) {
                try {
                    String json = entry.getValue().toString();
                    Map<String, Object> pilot = objectMapper.readValue(json, Map.class);

                    Double lat = getDouble(pilot.get("latitude"));
                    Double lon = getDouble(pilot.get("longitude"));
                    if (lat == null || lon == null) continue;

                    if (swLng != null && swLat != null && neLng != null && neLat != null) {
                        if (!isInViewport(lon, lat, swLng, swLat, neLng, neLat)) continue;
                    }

                    Map<String, Object> feature = new HashMap<>();
                    feature.put("type", "Feature");
                    feature.put("geometry", Map.of("type", "Point", "coordinates", Arrays.asList(lon, lat)));

                    Map<String, Object> properties = new HashMap<>();
                    properties.put("cid", pilot.get("cid"));
                    properties.put("callsign", pilot.get("callsign"));
                    properties.put("altitude", pilot.get("altitude"));
                    properties.put("groundspeed", pilot.get("groundspeed"));
                    properties.put("heading", pilot.get("heading"));
                    properties.put("server", pilot.get("server"));
                    properties.put("name", pilot.get("name"));
                    properties.put("trailUrl", "/map/pilot/" + pilot.get("cid") + "/trail");
                    properties.put("timestamp", System.currentTimeMillis());

                    Map<String, Object> flightPlan = (Map<String, Object>) pilot.get("flight_plan");
                    if (flightPlan != null) {
                        properties.put("departure", flightPlan.get("departure"));
                        properties.put("arrival", flightPlan.get("arrival"));
                        properties.put("alternate", flightPlan.get("alternate"));
                        properties.put("aircraft", flightPlan.get("aircraft"));
                        properties.put("flight_rules", flightPlan.get("flight_rules"));
                        properties.put("route", flightPlan.get("route"));
                    }

                    feature.put("properties", properties);
                    features.add(feature);

                } catch (Exception e) {
                    log.error("Error processing pilot {}: {}", entry.getKey(), e.getMessage());
                }
            }
        }

        Map<String, Object> geoJson = new HashMap<>();
        geoJson.put("type", "FeatureCollection");
        geoJson.put("features", features);
        geoJson.put("timestamp", System.currentTimeMillis());
        geoJson.put("totalPilots", allPilots != null ? allPilots.size() : 0);
        geoJson.put("visiblePilots", features.size());

        return geoJson;
    }

    @Override
    public Map<String, Object> getPilotTrailGeoJson(String cid) {
        String trailKey = "vatsim:pilot:" + cid + ":trail";
        List<String> pointsJson = redisTemplate.opsForList().range(trailKey, 0, -1);

        List<Map<String, Object>> coordinates = new ArrayList<>();
        if (pointsJson != null) {
            for (String pointJson : pointsJson) {
                try {
                    Map<String, Object> point = objectMapper.readValue(pointJson, Map.class);
                    Double lat = getDouble(point.get("lat"));
                    Double lon = getDouble(point.get("lon"));
                    if (lat != null && lon != null) {
                        coordinates.add(Map.of("lat", lat, "lon", lon, "ts", point.get("ts")));
                    }
                } catch (Exception e) {
                    log.error("Error parsing trail point for {}: {}", cid, e.getMessage());
                }
            }
        }

        Map<String, Object> geoJson = new HashMap<>();
        geoJson.put("type", "Feature");
        geoJson.put("geometry", Map.of(
                "type", "LineString",
                "coordinates", coordinates.stream()
                        .map(p -> Arrays.asList(p.get("lon"), p.get("lat")))
                        .toList()
        ));
        geoJson.put("properties", Map.of(
                "cid", cid,
                "pointsCount", coordinates.size()
        ));
        geoJson.put("timestamp", System.currentTimeMillis());

        return geoJson;
    }

    @Override
    public Map<String, Object> getPilotsPage(int page, int size) {
        Map<Object, Object> allPilots = redisTemplate.opsForHash().entries(REDIS_PILOTS_KEY);
        List<Map<String, Object>> pilotsList = new ArrayList<>();

        if (allPilots != null && !allPilots.isEmpty()) {
            for (Map.Entry<Object, Object> entry : allPilots.entrySet()) {
                try {
                    String json = entry.getValue().toString();
                    Map<String, Object> pilot = objectMapper.readValue(json, Map.class);
                    Map<String, Object> flightPlan = (Map<String, Object>) pilot.get("flight_plan");

                    Map<String, Object> pilotData = new HashMap<>();
                    pilotData.put("cid", pilot.get("cid"));
                    pilotData.put("callsign", pilot.get("callsign"));
                    pilotData.put("latitude", pilot.get("latitude"));
                    pilotData.put("longitude", pilot.get("longitude"));
                    pilotData.put("altitude", pilot.get("altitude"));
                    pilotData.put("groundspeed", pilot.get("groundspeed"));
                    pilotData.put("heading", pilot.get("heading"));
                    pilotData.put("server", pilot.get("server"));
                    pilotData.put("name", pilot.get("name"));

                    if (flightPlan != null) {
                        pilotData.put("departure", flightPlan.get("departure"));
                        pilotData.put("arrival", flightPlan.get("arrival"));
                        pilotData.put("alternate", flightPlan.get("alternate"));
                        pilotData.put("aircraft", flightPlan.get("aircraft"));
                        pilotData.put("flight_rules", flightPlan.get("flight_rules"));
                        pilotData.put("route", flightPlan.get("route"));
                    }

                    pilotsList.add(pilotData);

                } catch (Exception e) {
                    log.error("Error parsing pilot {}: {}", entry.getKey(), e.getMessage());
                }
            }
        }

        int total = pilotsList.size();
        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, total);
        List<Map<String, Object>> pageData = fromIndex < total ? pilotsList.subList(fromIndex, toIndex) : new ArrayList<>();

        Map<String, Object> result = new HashMap<>();
        result.put("page", page);
        result.put("size", size);
        result.put("total", total);
        result.put("pilots", pageData);

        return result;
    }

    @Override
    public Map<String, Object> generateControllersGeoJson(Double swLng, Double swLat, Double neLng, Double neLat) {
        Map<Object, Object> allControllers = redisTemplate.opsForHash().entries(REDIS_CONTROLLERS_KEY);
        List<Map<String, Object>> features = new ArrayList<>();

        if (allControllers != null && !allControllers.isEmpty()) {
            for (Map.Entry<Object, Object> entry : allControllers.entrySet()) {
                try {
                    String json = entry.getValue().toString();
                    Map<String, Object> controller = objectMapper.readValue(json, Map.class);
                    String callsign = (String) controller.get("callsign");
                    if (callsign == null) continue;

                    ResponseMessage<?> regionResponse = regionService.getCoordinatesById(callsign);
                    if (regionResponse.getCode() != 200 || regionResponse.getData() == null) continue;

                    @SuppressWarnings("unchecked")
                    List<List<Double>> coordinates = (List<List<Double>>) regionResponse.getData();

                    boolean inView = coordinates.stream().anyMatch(coord ->
                            coord.size() == 2 && isInViewport(coord.get(0), coord.get(1), swLng, swLat, neLng, neLat)
                    );
                    if (!inView) continue;

                    Map<String, Object> feature = new HashMap<>();
                    feature.put("type", "Feature");
                    feature.put("geometry", Map.of("type", "Polygon", "coordinates", List.of(coordinates)));

                    Map<String, Object> properties = new HashMap<>();
                    properties.put("cid", controller.get("cid"));
                    properties.put("callsign", controller.get("callsign"));
                    properties.put("frequency", controller.get("frequency"));
                    properties.put("facility", controller.get("facility"));
                    properties.put("rating", controller.get("rating"));
                    properties.put("name", controller.get("name"));
                    properties.put("server", controller.get("server"));
                    feature.put("properties", properties);

                    features.add(feature);

                } catch (Exception e) {
                    log.error("Error processing controller {}: {}", entry.getKey(), e.getMessage());
                }
            }
        }

        Map<String, Object> geoJson = new HashMap<>();
        geoJson.put("type", "FeatureCollection");
        geoJson.put("features", features);
        geoJson.put("timestamp", System.currentTimeMillis());
        geoJson.put("totalControllers", allControllers != null ? allControllers.size() : 0);
        geoJson.put("visibleControllers", features.size());
        return geoJson;
    }

    @Override
    public Map<String, Object> getControllersPage(int page, int size) {
        Map<Object, Object> allControllers = redisTemplate.opsForHash().entries(REDIS_CONTROLLERS_KEY);
        List<Map<String, Object>> controllersList = new ArrayList<>();

        if (allControllers != null && !allControllers.isEmpty()) {
            for (Map.Entry<Object, Object> entry : allControllers.entrySet()) {
                try {
                    String json = entry.getValue().toString();
                    Map<String, Object> controller = objectMapper.readValue(json, Map.class);

                    Map<String, Object> controllerData = new HashMap<>();
                    controllerData.put("cid", controller.get("cid"));
                    controllerData.put("callsign", controller.get("callsign"));
                    controllerData.put("frequency", controller.get("frequency"));
                    controllerData.put("facility", controller.get("facility"));
                    controllerData.put("rating", controller.get("rating"));
                    controllerData.put("name", controller.get("name"));
                    controllerData.put("server", controller.get("server"));

                    controllersList.add(controllerData);
                } catch (Exception e) {
                    log.error("Error parsing controller {}: {}", entry.getKey(), e.getMessage());
                }
            }
        }

        int total = controllersList.size();
        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, total);
        List<Map<String, Object>> pageData = fromIndex < total ? controllersList.subList(fromIndex, toIndex) : new ArrayList<>();

        Map<String, Object> result = new HashMap<>();
        result.put("page", page);
        result.put("size", size);
        result.put("total", total);
        result.put("controllers", pageData);

        return result;
    }

    private boolean isInViewport(double lon, double lat, double swLng, double swLat, double neLng, double neLat) {
        return lon >= swLng && lon <= neLng && lat >= swLat && lat <= neLat;
    }

    private Double getDouble(Object value) {
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).doubleValue();
        try { return Double.parseDouble(value.toString()); } catch (Exception e) { return null; }
    }
}
