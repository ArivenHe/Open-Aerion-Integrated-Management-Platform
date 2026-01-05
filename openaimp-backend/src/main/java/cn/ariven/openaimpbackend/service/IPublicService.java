package cn.ariven.openaimpbackend.service;

import cn.ariven.openaimpbackend.dto.ResponseMessage;
import cn.ariven.openaimpbackend.entity.BotQQ;
import java.util.List;

public interface IPublicService {
    ResponseMessage<List<BotQQ>> getBotConfig();
}
