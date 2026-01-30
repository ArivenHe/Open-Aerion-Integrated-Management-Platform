package cn.ariven.openaimpbackend.service.impl;

import cn.ariven.openaimpbackend.common.DatabaseManager;
import cn.ariven.openaimpbackend.common.Result;
import cn.ariven.openaimpbackend.dto.request.RequestRoute;
import cn.ariven.openaimpbackend.service.RouteService;
import cn.ariven.openaimpbackend.dto.response.ResponseFlightRoute;
import cn.ariven.openaimpbackend.util.GeoUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RouteServiceImpl implements RouteService {
    private Map<String, Airport> airportMap = new HashMap<>();
    private Map<Integer, Waypoint> waypointMap = new HashMap<>();
    private Map<String, List<Waypoint>> waypointByIdent = new HashMap<>();
    private Map<Integer, List<Edge>> adjacencyList = new HashMap<>();
    private List<Waypoint> allWaypoints = new ArrayList<>();
    private Set<Integer> connectedWaypoints = new HashSet<>();

    private boolean initialized = false;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Airport {
        private int airportId;
        private String ident;
        private double lat;
        private double lon;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Waypoint {
        private int waypointId;
        private String ident;
        private double lat;
        private double lon;
        private int airportId;
    }

    @Data
    @AllArgsConstructor
    private static class Edge {
        int targetId;
        double weight;
        String airwayName;
    }

    @Data
    @AllArgsConstructor
    private static class PathNode implements Comparable<PathNode> {
        int waypointId;
        double totalDistance;
        List<String> pathSegments;
        List<Integer> visitedWaypointIds;

        @Override
        public int compareTo(PathNode o) {
            return Double.compare(this.totalDistance, o.totalDistance);
        }
    }

    private synchronized void init() {
        if (initialized) return;
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {

            log.info("Loading airports...");
            ResultSet rs = stmt.executeQuery("SELECT airport_id, ident, laty, lonx FROM airport");
            while (rs.next()) {
                Airport a = new Airport();
                a.setAirportId(rs.getInt("airport_id"));
                a.setIdent(rs.getString("ident"));
                a.setLat(rs.getDouble("laty"));
                a.setLon(rs.getDouble("lonx"));
                airportMap.put(a.getIdent(), a);
            }

            log.info("Loading waypoints...");
            rs = stmt.executeQuery("SELECT waypoint_id, ident, laty, lonx, airport_id FROM waypoint");
            while (rs.next()) {
                Waypoint w = new Waypoint();
                w.setWaypointId(rs.getInt("waypoint_id"));
                w.setIdent(rs.getString("ident"));
                w.setLat(rs.getDouble("laty"));
                w.setLon(rs.getDouble("lonx"));
                w.setAirportId(rs.getInt("airport_id"));
                waypointMap.put(w.getWaypointId(), w);
                waypointByIdent.computeIfAbsent(w.getIdent(), k -> new ArrayList<>()).add(w);
                allWaypoints.add(w);
            }

            log.info("Loading airways...");
            rs = stmt.executeQuery("SELECT airway_name, from_waypoint_id, to_waypoint_id, direction FROM airway");
            while (rs.next()) {
                int from = rs.getInt("from_waypoint_id");
                int to = rs.getInt("to_waypoint_id");
                String name = rs.getString("airway_name");
                String direction = rs.getString("direction");

                Waypoint wFrom = waypointMap.get(from);
                Waypoint wTo = waypointMap.get(to);

                if (wFrom != null && wTo != null) {
                    connectedWaypoints.add(from);
                    connectedWaypoints.add(to);

                    double dist = GeoUtil.distance(wFrom.getLat(), wFrom.getLon(), wTo.getLat(), wTo.getLon());

                    adjacencyList.computeIfAbsent(from, k -> new ArrayList<>()).add(new Edge(to, dist, name));

                    if (!"F".equals(direction)) {
                        adjacencyList.computeIfAbsent(to, k -> new ArrayList<>()).add(new Edge(from, dist, name));
                    }
                }
            }

            initialized = true;
            log.info("Data loaded. Airports: {}, Waypoints: {}, Airways loaded.", airportMap.size(), waypointMap.size());

        } catch (Exception e) {
            log.error("Failed to initialize flight data", e);
            throw new RuntimeException("Failed to initialize flight data", e);
        }
    }


    private ResponseFlightRoute findShortestPath(String startICAO, String endICAO) {
        if (!initialized) {
            init();
        }

        Airport startAirport = airportMap.get(startICAO);
        Airport endAirport = airportMap.get(endICAO);

        if (startAirport == null || endAirport == null) {
            log.error("Airport not found: {} or {}", startICAO, endICAO);
            return null;
        }


        Set<Integer> validSidIds = new HashSet<>(getSidExitPoints(startICAO));
        List<Integer> entryPoints = new ArrayList<>(validSidIds);
        if (entryPoints.isEmpty()) {
            entryPoints = findNearestWaypoints(startAirport, 5);
        }

        Set<Integer> validStarIds = new HashSet<>(getStarEntryPoints(endICAO));
        List<Integer> exitPoints = new ArrayList<>(validStarIds);
        if (exitPoints.isEmpty()) {
            exitPoints = findNearestWaypoints(endAirport, 5);
        }

        log.info("Found {} entry points for {}", entryPoints.size(), startICAO);
        log.info("Found {} exit points for {}", exitPoints.size(), endICAO);


        PriorityQueue<PathNode> pq = new PriorityQueue<>();
        Map<Integer, Double> minDist = new HashMap<>();


        for (Integer wpId : entryPoints) {
            Waypoint w = waypointMap.get(wpId);
            double dist = GeoUtil.distance(startAirport.getLat(), startAirport.getLon(), w.getLat(), w.getLon());
            List<String> path = new ArrayList<>();
            path.add(startICAO);
            path.add("DCT");
            path.add(w.getIdent());
            List<Integer> visitedIds = new ArrayList<>();
            visitedIds.add(wpId);
            pq.add(new PathNode(wpId, dist, path, visitedIds));
            minDist.put(wpId, dist);
        }

        PathNode bestPath = null;
        Set<Integer> visited = new HashSet<>();
        int nodesVisited = 0;

        while (!pq.isEmpty()) {
            PathNode current = pq.poll();

            if (visited.contains(current.waypointId)) continue;
            visited.add(current.waypointId);
            nodesVisited++;

            if (nodesVisited % 1000 == 0) {
                log.debug("Visited {} nodes, current dist: {}", nodesVisited, current.totalDistance);
            }


            if (exitPoints.contains(current.waypointId)) {
                Waypoint w = waypointMap.get(current.waypointId);
                double distToEnd = GeoUtil.distance(w.getLat(), w.getLon(), endAirport.getLat(), endAirport.getLon());
                double totalDist = current.totalDistance + distToEnd;


                if (bestPath == null || totalDist < bestPath.totalDistance) {
                    List<String> fullPath = new ArrayList<>(current.pathSegments);
                    fullPath.add("DCT");
                    fullPath.add(endICAO);
                    bestPath = new PathNode(-1, totalDist, fullPath, current.visitedWaypointIds);
                }
            }


            if (bestPath != null && current.totalDistance > bestPath.totalDistance) {
                continue;
            }

            List<Edge> neighbors = adjacencyList.get(current.waypointId);
            if (neighbors != null) {
                for (Edge edge : neighbors) {
                    double newDist = current.totalDistance + edge.weight;
                    if (newDist < minDist.getOrDefault(edge.targetId, Double.MAX_VALUE)) {
                        minDist.put(edge.targetId, newDist);
                        List<String> newPath = new ArrayList<>(current.pathSegments);
                        newPath.add(edge.airwayName);
                        Waypoint targetWp = waypointMap.get(edge.targetId);
                        newPath.add(targetWp.getIdent());
                        List<Integer> newVisitedIds = new ArrayList<>(current.visitedWaypointIds);
                        newVisitedIds.add(edge.targetId);
                        pq.add(new PathNode(edge.targetId, newDist, newPath, newVisitedIds));
                    }
                }
            }
        }

        if (bestPath != null) {
            String compactedPath = compactPath(bestPath.pathSegments);

            String firstWaypoint = null;
            String lastWaypoint = null;

            if (bestPath.pathSegments.size() >= 3) {
                firstWaypoint = bestPath.pathSegments.get(2);
            }
            if (bestPath.pathSegments.size() >= 3) {
                lastWaypoint = bestPath.pathSegments.get(bestPath.pathSegments.size() - 3);
            }

            final String fWp = firstWaypoint;
            boolean hasSID = fWp != null && validSidIds.stream()
                    .anyMatch(id -> waypointMap.get(id).getIdent().equals(fWp));

            final String lWp = lastWaypoint;
            boolean hasSTAR = lWp != null && validStarIds.stream()
                    .anyMatch(id -> waypointMap.get(id).getIdent().equals(lWp));

            String sidName = hasSID ? "SID" : null;
            String starName = hasSTAR ? "STAR" : null;

            if (hasSID) {
                if (compactedPath.startsWith(startICAO + " DCT " + firstWaypoint)) {
                    compactedPath = compactedPath.replaceFirst(startICAO + " DCT " + firstWaypoint, startICAO + " SID " + firstWaypoint);
                } else {
                    compactedPath = compactedPath.replaceFirst(" DCT " + firstWaypoint, " SID " + firstWaypoint);
                }
            }

            if (hasSTAR) {
                if (compactedPath.endsWith(lastWaypoint + " DCT " + endICAO)) {
                    compactedPath = compactedPath.replace(lastWaypoint + " DCT " + endICAO, lastWaypoint + " STAR " + endICAO);
                }
            }

            List<String> sids = new ArrayList<>();
            List<String> stars = new ArrayList<>();

            if (hasSID) {
                sids = getProcedureNames(startICAO, "D", firstWaypoint);
            } else {
                 sids = getProcedureNames(startICAO, "D", null);
            }

            if (hasSTAR) {
                stars = getProcedureNames(endICAO, "A", lastWaypoint);
            } else {
                stars = getProcedureNames(endICAO, "A", null);
            }

            List<String> runways = new ArrayList<>();
            runways.addAll(getRunwayNames(startICAO));
            runways.addAll(getRunwayNames(endICAO));

            List<ResponseFlightRoute.RoutePoint> routePoints = new ArrayList<>();
            List<List<Double>> coordinates = new ArrayList<>();

            // Start Airport
            if (startAirport != null) {
                routePoints.add(new ResponseFlightRoute.RoutePoint(startAirport.getIdent(), startAirport.getLat(), startAirport.getLon(), "Airport"));
                coordinates.add(Arrays.asList(startAirport.getLon(), startAirport.getLat()));
            }

            // Waypoints
            if (bestPath.visitedWaypointIds != null) {
                for (Integer wpId : bestPath.visitedWaypointIds) {
                    Waypoint wp = waypointMap.get(wpId);
                    if (wp != null) {
                        routePoints.add(new ResponseFlightRoute.RoutePoint(wp.getIdent(), wp.getLat(), wp.getLon(), "Waypoint"));
                        coordinates.add(Arrays.asList(wp.getLon(), wp.getLat()));
                    }
                }
            }

            // End Airport
            if (endAirport != null) {
                routePoints.add(new ResponseFlightRoute.RoutePoint(endAirport.getIdent(), endAirport.getLat(), endAirport.getLon(), "Airport"));
                coordinates.add(Arrays.asList(endAirport.getLon(), endAirport.getLat()));
            }

            // GeoJSON construction
            Map<String, Object> geometry = new HashMap<>();
            geometry.put("type", "LineString");
            geometry.put("coordinates", coordinates);

            Map<String, Object> feature = new HashMap<>();
            feature.put("type", "Feature");
            feature.put("geometry", geometry);
            feature.put("properties", new HashMap<>());

            Map<String, Object> geoJson = new HashMap<>();
            geoJson.put("type", "FeatureCollection");
            geoJson.put("features", Collections.singletonList(feature));

            return ResponseFlightRoute.builder()
                    .startICAO(startICAO)
                    .endICAO(endICAO)
                    .totalDistance(BigDecimal.valueOf(bestPath.totalDistance).setScale(2, RoundingMode.HALF_UP).doubleValue())
                    .routeString(compactedPath)
                    .waypoints(routePoints)
                    .geoJson(geoJson)
                    .build();
        } else {
            return null;
        }
    }



    private String compactPath(List<String> pathSegments) {
        if (pathSegments == null || pathSegments.isEmpty()) return "";



        List<String> result = new ArrayList<>();
        if (pathSegments.isEmpty()) return "";

        result.add(pathSegments.get(0));

        int i = 1;
        while (i < pathSegments.size()) {
            String airway = pathSegments.get(i);
            String nextPoint = pathSegments.get(i+1);

            while (i + 2 < pathSegments.size() && pathSegments.get(i+2).equals(airway)) {
                nextPoint = pathSegments.get(i+3);
                i += 2;
            }

            result.add(airway);
            result.add(nextPoint);
            i += 2;
        }

        return String.join(" ", result);
    }

    private List<Integer> getSidExitPoints(String airportIdent) {
        String sql = "SELECT al.fix_ident " +
                "FROM approach a " +
                "JOIN approach_leg al ON a.approach_id = al.approach_id " +
                "WHERE a.airport_ident = ? AND a.suffix = 'D' " +
                "UNION " +
                "SELECT t.fix_ident " +
                "FROM transition t " +
                "JOIN approach a ON t.approach_id = a.approach_id " +
                "WHERE a.airport_ident = ? AND a.suffix = 'D'";
        return getProcedurePoints(airportIdent, sql);
    }

    private List<Integer> getStarEntryPoints(String airportIdent) {
        String sql = "SELECT al.fix_ident " +
                "FROM approach a " +
                "JOIN approach_leg al ON a.approach_id = al.approach_id " +
                "WHERE a.airport_ident = ? AND a.suffix = 'A' " +
                "UNION " +
                "SELECT t.fix_ident " +
                "FROM transition t " +
                "JOIN approach a ON t.approach_id = a.approach_id " +
                "WHERE a.airport_ident = ? AND a.suffix = 'A'";
        return getProcedurePoints(airportIdent, sql);
    }

    private List<Integer> getProcedurePoints(String airportIdent, String sql) {
        Set<String> fixIdents = new HashSet<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, airportIdent);
            if (sql.contains("UNION")) {
                pstmt.setString(2, airportIdent);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    fixIdents.add(rs.getString("fix_ident"));
                }
            }
        } catch (Exception e) {
            log.error("Error getting procedure points for {}", airportIdent, e);
        }

        List<Integer> result = new ArrayList<>();
        Airport airport = airportMap.get(airportIdent);

        for (String ident : fixIdents) {
            List<Waypoint> candidates = waypointByIdent.get(ident);
            if (candidates != null) {
                for (Waypoint w : candidates) {
                    if (connectedWaypoints.contains(w.getWaypointId())) {
                        if (airport != null && GeoUtil.distance(airport.getLat(), airport.getLon(), w.getLat(), w.getLon()) < 500) {
                            result.add(w.getWaypointId());
                        }
                    }
                }
            }
        }
        return result;
    }

    private List<Integer> findNearestWaypoints(Airport airport, int limit) {
        double boxSize = 5.0;

        return allWaypoints.stream()
                .filter(w -> connectedWaypoints.contains(w.getWaypointId()))
                .filter(w -> Math.abs(w.getLat() - airport.getLat()) < boxSize && Math.abs(w.getLon() - airport.getLon()) < boxSize)
                .sorted(Comparator.comparingDouble(w -> GeoUtil.distance(airport.getLat(), airport.getLon(), w.getLat(), w.getLon())))
                .limit(limit)
                .map(Waypoint::getWaypointId)
                .collect(Collectors.toList());
    }

    @Override
    public Result<ResponseFlightRoute> getRoute(RequestRoute requestRoute) {
        ResponseFlightRoute vo = findShortestPath(requestRoute.getDepIcao(), requestRoute.getArrICao());
        if (vo == null) {
            return Result.failed("Route not found");
        }
        return Result.success(vo);
    }

    private List<String> getProcedureNames(String airportIdent, String suffix, String fixIdent) {
        List<String> names = new ArrayList<>();
        String sql;
        
        if (fixIdent != null) {
            // Updated query: join approach_leg and transition tables to find procedures containing the fix
            sql = "SELECT DISTINCT a.arinc_name " +
                  "FROM approach a " +
                  "LEFT JOIN approach_leg al ON a.approach_id = al.approach_id " +
                  "LEFT JOIN transition t ON a.approach_id = t.approach_id " +
                  "WHERE a.airport_ident = ? AND a.suffix = ? " +
                  "AND (al.fix_ident = ? OR t.fix_ident = ?)";
        } else {
             sql = "SELECT DISTINCT arinc_name FROM approach WHERE airport_ident = ? AND suffix = ?";
        }

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, airportIdent);
            pstmt.setString(2, suffix);
            if (fixIdent != null) {
                pstmt.setString(3, fixIdent);
                pstmt.setString(4, fixIdent);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    names.add(rs.getString("arinc_name"));
                }
            }
        } catch (Exception e) {
            log.error("Error getting procedure names for {}", airportIdent, e);
        }
        return names;
    }

    private List<String> getRunwayNames(String airportIdent) {
        List<String> names = new ArrayList<>();
        Airport airport = airportMap.get(airportIdent);
        if (airport == null) return names;
        
        // Runways are stored in runway_end table linked via runway table
        String sql = "SELECT re.name FROM runway r " +
                     "JOIN runway_end re ON r.primary_end_id = re.runway_end_id " +
                     "WHERE r.airport_id = ? " +
                     "UNION " +
                     "SELECT re.name FROM runway r " +
                     "JOIN runway_end re ON r.secondary_end_id = re.runway_end_id " +
                     "WHERE r.airport_id = ?";
                     
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, airport.getAirportId());
            pstmt.setInt(2, airport.getAirportId());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    names.add(rs.getString("name"));
                }
            }
        } catch (Exception e) {
            log.error("Error getting runways for {}", airportIdent, e);
        }
        return names;
    }
}
