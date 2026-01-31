package cn.ariven.openaimpbackend.service;

import cn.ariven.openaimpbackend.common.Result;
import cn.ariven.openaimpbackend.dto.request.common.RequestRoute;
import cn.ariven.openaimpbackend.dto.response.common.ResponseFlightRoute;

public interface RouteService {
    Result<ResponseFlightRoute> getRoute(RequestRoute requestRoute);
}
