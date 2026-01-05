package cn.ariven.openaimpbackend.controller;

import cn.ariven.openaimpbackend.service.IVatsimGeoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/Map")
@RequiredArgsConstructor
public class MapController {

    private final IVatsimGeoService geoService;

    // ===========================
    // 飞行员 GeoJSON
    // ===========================
    @GetMapping("/pilots/geojson")
    public Map<String, Object> getPilots(
            @RequestParam(required = false) Double swLng,
            @RequestParam(required = false) Double swLat,
            @RequestParam(required = false) Double neLng,
            @RequestParam(required = false) Double neLat) {
        return geoService.generatePilotsGeoJson(swLng, swLat, neLng, neLat);
    }

    // ===========================
    // 管制员 GeoJSON
    // ===========================
    @GetMapping("/controllers/geojson")
    public Map<String, Object> getControllers(
            @RequestParam(required = false) Double swLng,
            @RequestParam(required = false) Double swLat,
            @RequestParam(required = false) Double neLng,
            @RequestParam(required = false) Double neLat) {
        return geoService.generateControllersGeoJson(swLng, swLat, neLng, neLat);
    }

    // ===========================
    // 飞行员分页
    // ===========================
    @GetMapping("/pilots/page")
    public Map<String, Object> getPilotsPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return geoService.getPilotsPage(page, size);
    }

    // ===========================
    // 管制员分页
    // ===========================
    @GetMapping("/controllers/page")
    public Map<String, Object> getControllersPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return geoService.getControllersPage(page, size);
    }

    // ===========================
    // 拉取飞行员实时数据
    // ===========================
    @PostMapping("/pilots/fetch")
    public String fetchPilots() {
        Map<String, Object> data = geoService.fetchAndStorePilots();
        int count = 0;
        if (data.containsKey("pilots") && data.get("pilots") instanceof java.util.List) {
            count = ((java.util.List<?>) data.get("pilots")).size();
        }
        return "Fetched " + count + " pilots";
    }

    // ===========================
    // 单个飞行员轨迹
    // ===========================
    @GetMapping("/pilot/{cid}/trail")
    public Map<String, Object> getPilotTrail(@PathVariable String cid) {
        return geoService.getPilotTrailGeoJson(cid);
    }
}
