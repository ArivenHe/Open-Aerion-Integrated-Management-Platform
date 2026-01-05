package cn.ariven.openaimpbackend.service;

import java.util.Map;

public interface IVatsimGeoService {
    Map<String, Object> fetchAndStorePilots();
    Map<String, Object> generatePilotsGeoJson(Double swLng, Double swLat, Double neLng, Double neLat);
    Map<String, Object> getPilotTrailGeoJson(String cid);
    Map<String, Object> getPilotsPage(int page, int size);
    Map<String, Object> generateControllersGeoJson(Double swLng, Double swLat, Double neLng, Double neLat);
    Map<String, Object> getControllersPage(int page, int size);
}
