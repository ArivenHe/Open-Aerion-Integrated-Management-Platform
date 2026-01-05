package cn.ariven.openaimpbackend.service;

import cn.ariven.openaimpbackend.dto.FsdJwtDTO;
import cn.ariven.openaimpbackend.dto.OnlineUserDto;
import cn.ariven.openaimpbackend.dto.ResponseMessage;

import java.util.Map;

public interface IFsdService {
    ResponseMessage<Map<String, Object>> login(String identifier, String password);
    ResponseMessage<String> onDisconnect(OnlineUserDto onlineUserDto);
    Object fsdJwt(FsdJwtDTO fsdJwtDTO);
}
