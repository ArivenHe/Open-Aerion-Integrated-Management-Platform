package cn.ariven.openaimpbackend.controller;

import cn.ariven.openaimpbackend.dto.ResponseMessage;
import cn.ariven.openaimpbackend.dto.RouteSearchDTO;
import cn.ariven.openaimpbackend.service.IWeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public/search/text")
@RequiredArgsConstructor
public class SearchController {

    private final IWeatherService weatherService;

    @GetMapping("/Weather/{icao}")
    public ResponseMessage<Object> getAllWeather(@PathVariable String icao) {
        return weatherService.getAllWeather(icao);
    }

    @GetMapping("/Airport/{icao}")
    public ResponseMessage<Object> getAllAirport(@PathVariable String icao) {
        return weatherService.getAirportInfo(icao);
    }

    @GetMapping("/Atis/{icao}")
    public ResponseMessage<Object> getAtis(@PathVariable String icao) {
        return weatherService.getAirportAtis(icao);
    }

    @PostMapping("/Route")
    public ResponseMessage<Object> getRoute(@RequestBody RouteSearchDTO routeSearchDTO) {
        return weatherService.getRoute(routeSearchDTO);
    }
}
