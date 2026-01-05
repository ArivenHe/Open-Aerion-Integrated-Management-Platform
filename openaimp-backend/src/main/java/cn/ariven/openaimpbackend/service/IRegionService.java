package cn.ariven.openaimpbackend.service;

import cn.ariven.openaimpbackend.dto.ResponseMessage;

public interface IRegionService {
    ResponseMessage<?> getCoordinatesById(String id);
}
