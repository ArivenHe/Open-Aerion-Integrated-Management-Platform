package cn.ariven.openaimpbackend.service;

import cn.ariven.openaimpbackend.common.Result;
import cn.ariven.openaimpbackend.dto.request.common.RequestAtisInfo;
import cn.ariven.openaimpbackend.dto.request.common.RequestMetar;
import cn.ariven.openaimpbackend.dto.response.common.ResponseAtisInfo;
import cn.ariven.openaimpbackend.dto.response.common.ResponseMetar;
import io.github.mivek.exception.ParseException;

public interface WeatherService {
    Result<ResponseMetar> getMetar(RequestMetar requestMetar);
    Result<ResponseAtisInfo> getAtis(RequestAtisInfo requestAtisInfo) throws ParseException;
}
