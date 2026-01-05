package cn.ariven.openaimpbackend.service;

import cn.ariven.openaimpbackend.dto.ResponseMessage;
import cn.ariven.openaimpbackend.dto.RouteSearchDTO;

public interface IWeatherService {
    ResponseMessage<Object> getAllWeather(String icao);
    ResponseMessage<Object> getAirportInfo(String icao);
    ResponseMessage<Object> getAirportAtis(String icao);
    ResponseMessage<Object> getRoute(RouteSearchDTO routeSearchDTO);
}
