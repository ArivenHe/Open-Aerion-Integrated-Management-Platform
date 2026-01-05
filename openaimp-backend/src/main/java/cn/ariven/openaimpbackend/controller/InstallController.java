package cn.ariven.openaimpbackend.controller;

import cn.ariven.openaimpbackend.dto.ResponseMessage;
import cn.ariven.openaimpbackend.service.IStartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/Install")
@RequiredArgsConstructor
public class InstallController {
    private final IStartService startService;

    @GetMapping("/FirstInstall")
    public ResponseMessage<Boolean> firstInstall() {
        return ResponseMessage.success(startService.isFirstInstall());
    }
}
