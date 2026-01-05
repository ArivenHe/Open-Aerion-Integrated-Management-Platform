package cn.ariven.openaimpbackend.service.impl;

import cn.ariven.openaimpbackend.dto.Region;
import cn.ariven.openaimpbackend.dto.ResponseMessage;
import cn.ariven.openaimpbackend.service.IRegionService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class RegionServiceImpl implements IRegionService {

    private final List<Region> regionList;

    public RegionServiceImpl() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        regionList = mapper.readValue(
                new ClassPathResource("region.json").getInputStream(),
                new TypeReference<>() {}
        );
        log.info("Loaded {} regions", regionList.size());
    }

    @Override
    public ResponseMessage<?> getCoordinatesById(String id) {
        log.info("Looking up region coordinates for ID: {}", id);

        String[] parts = id.split("_");

        if (parts.length == 2) {
            String suffix = parts[1].toUpperCase();
            if (suffix.equals("CTR") || suffix.equals("FSS")) {
                String lookupId = parts[0].toUpperCase();

                return regionList.stream()
                        .filter(region -> region.getId().equalsIgnoreCase(lookupId))
                        .findFirst()
                        .map(region -> {
                            log.info("Found region: {}", region.getId());
                            // 提取第一个 ring 的坐标点列表
                            List<List<Double>> coordinates = region.getCoordinates()
                                    .stream()
                                    .findFirst()
                                    .flatMap(level1 -> level1.stream().findFirst())
                                    .orElse(null);

                            if (coordinates == null) {
                                return ResponseMessage.error("Region data malformed");
                            }

                            return ResponseMessage.success(coordinates);
                        })
                        .orElseGet(() -> {
                            log.warn("Region ID '{}' not found", lookupId);
                            return ResponseMessage.error("Region not found: " + lookupId);
                        });
            }
        }

        log.warn("Unsupported ID format: {}", id);
        return ResponseMessage.error("Invalid ID format or unsupported suffix (only CTR/FSS allowed)");
    }
}
