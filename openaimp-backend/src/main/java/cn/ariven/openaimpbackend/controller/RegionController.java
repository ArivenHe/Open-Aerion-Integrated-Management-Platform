package cn.ariven.openaimpbackend.controller;

import cn.ariven.openaimpbackend.dto.ResponseMessage;
import cn.ariven.openaimpbackend.service.IRegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public/map")
@RequiredArgsConstructor
public class RegionController {

    private final IRegionService regionService;

    @GetMapping("/region/{id}")
    public ResponseMessage<?> getCoordinatesById(@PathVariable String id) {
        return regionService.getCoordinatesById(id);
    }
}
