package cn.ariven.openaimpbackend.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.ariven.openaimpbackend.dto.ResponseMessage;
import cn.ariven.openaimpbackend.entity.BotQQ;
import cn.ariven.openaimpbackend.service.INodeServerService;
import cn.ariven.openaimpbackend.service.IPublicService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SaCheckRole("super-admin")
@RequestMapping("/ADMIN/Bot")
@RequiredArgsConstructor
public class BotServerController {

    private final INodeServerService nodeServerService;
    private final IPublicService publicService;

    @PostMapping("/start")
    public ResponseMessage<String> start() {
        return nodeServerService.startNodeServer();
    }

    @PostMapping("/stop")
    public ResponseMessage<String> stop() {
        return nodeServerService.stopNodeServer();
    }

    @GetMapping("/status")
    public ResponseMessage<String> status() {
        return nodeServerService.status();
    }

    @GetMapping("/logs")
    public ResponseMessage<String> logs(@RequestParam(defaultValue = "100") int lines) {
        return nodeServerService.getLogs(lines);
    }

    @PostMapping("/Update")
    public ResponseMessage<String> update(@RequestBody BotQQ qq) {
        return nodeServerService.updateBotConfig(qq);
    }

    @GetMapping("/GetInfo")
    public ResponseMessage<List<BotQQ>> getInfo() {
        return publicService.getBotConfig();
    }

    @GetMapping("/GetCommandList")
    public ResponseMessage<Object> getCommandList() {
        return nodeServerService.getList();
    }
}
