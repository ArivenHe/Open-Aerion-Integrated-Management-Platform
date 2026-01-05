package cn.ariven.openaimpbackend.controller;

import cn.ariven.openaimpbackend.dto.FsdJwtDTO;
import cn.ariven.openaimpbackend.dto.OnlineUserDto;
import cn.ariven.openaimpbackend.dto.ResponseMessage;
import cn.ariven.openaimpbackend.service.IFsdService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/public/api/v1/")
@RequiredArgsConstructor
public class FsdController {

    private final IFsdService fsdService;

    @PostMapping("/login")
    public ResponseMessage<Map<String, Object>> login(
            @RequestParam("user") String identifier,
            @RequestParam("password") String password
    ) {
        return fsdService.login(identifier, password);
    }

    @PostMapping("/cb/ondisconnect")
    public ResponseMessage<String> ondisconnect(@RequestBody OnlineUserDto onlineUserDto) {
        return fsdService.onDisconnect(onlineUserDto);
    }

    @PostMapping("/fsd-jwt")
    public Object fsdJwt(@RequestBody FsdJwtDTO fsdJwtDTO) {
        return fsdService.fsdJwt(fsdJwtDTO);
    }
}
