package cn.ariven.openaimpbackend.controller;

import cn.ariven.openaimpbackend.dto.ResponseMessage;
import cn.ariven.openaimpbackend.entity.BotQQ;
import cn.ariven.openaimpbackend.service.IPublicService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicController {
    private final IPublicService publicService;

    @GetMapping("/Bot/Config")
    public ResponseMessage<List<BotQQ>> getInfo() {
        return publicService.getBotConfig();
    }
}
