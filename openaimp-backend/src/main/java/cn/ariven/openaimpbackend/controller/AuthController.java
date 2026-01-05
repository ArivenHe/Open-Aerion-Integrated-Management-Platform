package cn.ariven.openaimpbackend.controller;

import cn.ariven.openaimpbackend.dto.ResponseMessage;
import cn.ariven.openaimpbackend.dto.UserDto;
import cn.ariven.openaimpbackend.service.IAuthService;
import cn.ariven.openaimpbackend.util.ClientUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/Auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/SendEmailCode")
    public ResponseMessage<String> sendEmailCode(HttpServletRequest request, @RequestParam String email) {
        String clientIp = ClientUtil.getClientIp(request);
        return authService.sendEmailCode(clientIp, email);
    }

    @PostMapping("/Register")
    public ResponseMessage<String> register(@RequestBody UserDto user) {
        return authService.register(user);
    }
}
