package cn.ariven.openaimpbackend.service.impl;

import cn.ariven.openaimpbackend.dto.ResponseMessage;
import cn.ariven.openaimpbackend.entity.BotQQ;
import cn.ariven.openaimpbackend.repository.BotQQRepository;
import cn.ariven.openaimpbackend.service.IPublicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicServiceImpl implements IPublicService {
    private final BotQQRepository botQQRepository;

    @Override
    public ResponseMessage<List<BotQQ>> getBotConfig() {
        return ResponseMessage.success((List<BotQQ>) botQQRepository.findAll());
    }
}
