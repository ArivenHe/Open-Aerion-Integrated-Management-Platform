package cn.ariven.openaimpbackend.service;

import cn.ariven.openaimpbackend.dto.ResponseMessage;
import cn.ariven.openaimpbackend.entity.BotQQ;

public interface INodeServerService {
    ResponseMessage<String> startNodeServer();
    ResponseMessage<String> stopNodeServer();
    ResponseMessage<String> status();
    ResponseMessage<String> getLogs(int lines);
    ResponseMessage<Object> getList();
    ResponseMessage<String> updateBotConfig(BotQQ qq);
}
