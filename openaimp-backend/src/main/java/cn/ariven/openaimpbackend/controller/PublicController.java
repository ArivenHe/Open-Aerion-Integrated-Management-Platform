package cn.ariven.openaimpbackend.controller;

import cn.ariven.openaimpbackend.common.Result;
import cn.ariven.openaimpbackend.dto.request.common.RequestAtisInfo;
import cn.ariven.openaimpbackend.dto.request.common.RequestMetar;
import cn.ariven.openaimpbackend.dto.request.common.RequestRoute;
import cn.ariven.openaimpbackend.dto.response.common.ResponseAtisInfo;
import cn.ariven.openaimpbackend.dto.response.common.ResponseFlightRoute;
import cn.ariven.openaimpbackend.dto.response.common.ResponseMetar;
import cn.ariven.openaimpbackend.service.RouteService;
import cn.ariven.openaimpbackend.service.WeatherService;
import io.github.mivek.exception.ParseException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicController {
    private final WeatherService weatherService;
    private final RouteService routeService;

    @PostMapping("/metar")
    public Result<ResponseMetar> getMetar(RequestMetar requestMetar) {
        return weatherService.getMetar(requestMetar);
    }

    @PostMapping("/atis")
    public Result<ResponseAtisInfo> getAtisInfo(RequestAtisInfo requestAtisInfo) throws ParseException {
        return weatherService.getAtis(requestAtisInfo);
    }

    @PostMapping("/route")
    public Result<ResponseFlightRoute> getRoute(RequestRoute requestRoute) {
        return routeService.getRoute(requestRoute);
    }

}
