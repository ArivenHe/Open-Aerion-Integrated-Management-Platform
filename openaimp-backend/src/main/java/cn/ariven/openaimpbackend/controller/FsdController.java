package cn.ariven.openaimpbackend.controller;

import cn.ariven.openaimpbackend.dto.request.auth.RequestFsdLogin;
import cn.ariven.openaimpbackend.dto.response.auth.ResponseFsdLoginFailure;
import cn.ariven.openaimpbackend.dto.response.auth.ResponseFsdLoginSuccess;
import cn.ariven.openaimpbackend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fsd")
public class FsdController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> fsdLogin(@RequestBody @Valid RequestFsdLogin request) {
        try {
            String token = userService.fsdLogin(request);
            return ResponseEntity.ok(new ResponseFsdLoginSuccess(true, token));
        } catch (Exception e) {
            String message = e.getMessage() == null ? "Login failed" : e.getMessage();
            HttpStatus status = isFsdAuthClientError(message) ? HttpStatus.UNAUTHORIZED : HttpStatus.INTERNAL_SERVER_ERROR;
            return ResponseEntity.status(status).body(new ResponseFsdLoginFailure(false, message));
        }
    }

    private boolean isFsdAuthClientError(String message) {
        return "Login failed".equals(message)
                || "CID not found".equals(message)
                || "Invalid password".equals(message)
                || "Network rating too low".equals(message)
                || "Network rating too high".equals(message);
    }
}
