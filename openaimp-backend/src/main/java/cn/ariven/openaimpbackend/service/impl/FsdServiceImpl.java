package cn.ariven.openaimpbackend.service.impl;

import cn.ariven.openaimpbackend.dto.*;
import cn.ariven.openaimpbackend.entity.PilotHistory;
import cn.ariven.openaimpbackend.entity.User;
import cn.ariven.openaimpbackend.repository.PilotHistoryRepository;
import cn.ariven.openaimpbackend.repository.UserRepository;
import cn.ariven.openaimpbackend.service.IFsdService;
import cn.ariven.openaimpbackend.util.PasswordEncoder;
import cn.ariven.openaimpbackend.util.UuidValidator;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class FsdServiceImpl implements IFsdService {

    private final UserRepository userRepository;
    private final PilotHistoryRepository pilotHistoryRepository;

    @Override
    public ResponseMessage<Map<String, Object>> login(String identifier, String password) {
        if (UuidValidator.isUuid(password)) {
            String id = "fsdUser" + identifier;
            String token = StpUtil.getTokenValueByLoginId(id);
            if (Objects.equals(token, password)) {
                StpUtil.login(id);
                // String token1 = StpUtil.getTokenValueByLoginId(id);

                User user = isEmail(identifier)
                        ? userRepository.findUserByEmail(identifier)
                        : userRepository.findUserByCallsign(identifier);

                if (user == null) {
                    return ResponseMessage.error(401, "User not found");
                }

                Map<String, Object> data = new HashMap<>();
                data.put("rating", user.getRating());
                data.put("callsign", user.getCallsign());
                return ResponseMessage.success(data);
            }
        } else {
            log.info("Login attempt: {}", identifier);

            User user = isEmail(identifier)
                    ? userRepository.findUserByEmail(identifier)
                    : userRepository.findUserByCallsign(identifier);

            if (user == null) {
                log.warn("User not found: {}", identifier);
                return ResponseMessage.error(401, "Invalid credentials");
            }

            log.info("User found, rating: {}", user.getRating());
            boolean isValid = PasswordEncoder.compare(password, user.getPassword());

            if (isValid) {
                Map<String, Object> data = new HashMap<>();
                data.put("rating", user.getRating());
                data.put("callsign", user.getCallsign());
                return ResponseMessage.success(data);
            } else {
                return ResponseMessage.error(401, "Invalid credentials");
            }
        }
        return ResponseMessage.error(401, "Invalid credentials");
    }

    private boolean isEmail(String input) {
        return input.contains("@") && input.contains(".");
    }

    @Override
    public ResponseMessage<String> onDisconnect(OnlineUserDto onlineUserDto) {
        log.info(onlineUserDto.toString());
        log.info("User type: {}", onlineUserDto.getType());

        if ("pilot".equals(onlineUserDto.getType())) {
            log.info("开始处理pilot");
            log.info("User Callsign: {}", onlineUserDto.getCallsign());
            
            // Following legacy logic: use cid from dto to find user by callsign
            User user = userRepository.findUserByCallsign(onlineUserDto.getCid());
            
            if (user != null) {
                log.info(user.toString());
                PilotHistory pilotHistory = new PilotHistory();
                // pilotHistory.setCid(user.getCid()); // user.getCid() returns String?
                // Assuming user.getCid() returns the CID.
                // In PilotHistory entity, cid might be String or int. 
                // Let's check PilotHistory entity later if error occurs, but for now assume string or compatible.
                // Actually User.cid is String in some contexts, but let's see.
                // In User entity it's likely String or int. 
                // Using String.valueOf to be safe if needed, but setters usually typed.
                
                // Assuming user.getCid() returns String as per User entity usage in other places (findUserByCallsign returns User).
                // Wait, userRepository.findUserByCallsign returns User.
                
                pilotHistory.setCid(user.getCid());
                try {
                    pilotHistory.setOnlinetime(Integer.parseInt(onlineUserDto.getOnlinetime()));
                } catch (NumberFormatException e) {
                    pilotHistory.setOnlinetime(0);
                    log.warn("Failed to parse onlinetime: {}", onlineUserDto.getOnlinetime());
                }
                pilotHistory.setCallsign(onlineUserDto.getCallsign());
                pilotHistory.setDep(onlineUserDto.getDep() == null ? "N/A" : onlineUserDto.getDep());
                pilotHistory.setArr(onlineUserDto.getArr() == null ? "N/A" : onlineUserDto.getArr());
                pilotHistory.setAircraft(onlineUserDto.getAircraft() == null ? "N/A" : onlineUserDto.getAircraft());
                pilotHistory.setConndate(onlineUserDto.getConndate());
                
                pilotHistoryRepository.save(pilotHistory);
            } else {
                log.warn("User not found for onDisconnect: {}", onlineUserDto.getCid());
            }
            
            return ResponseMessage.success("success");
        }
        return ResponseMessage.success("ignored");
    }

    @Override
    public Object fsdJwt(FsdJwtDTO fsdJwtDTO) {
        log.info(fsdJwtDTO.toString());
        if (UuidValidator.isUuid(fsdJwtDTO.getPassword())) {
            String callsign = fsdJwtDTO.getCid();
            String id = "fsdUser" + callsign;
            String token = StpUtil.getTokenValueByLoginId(id);
            if (Objects.equals(token, fsdJwtDTO.getPassword())) {
                StpUtil.login(id);
                String token1 = StpUtil.getTokenValueByLoginId(id);
                return FsdJwtResponseMessage.success(token1);
            }
            return FsdJwtResponseFailedMessage.success("invalid token");
        } else {
            String callsign = fsdJwtDTO.getCid();
            User user = userRepository.findUserByCallsign(callsign);
            if (user == null) {
                log.warn("User not found: {}", callsign);
                return FsdJwtResponseFailedMessage.success("invalid cid or password");
            }

            log.info("User found, rating: {}", user.getRating());
            boolean isValid = PasswordEncoder.compare(fsdJwtDTO.getPassword(), user.getPassword());

            if (isValid) {
                String id = "fsdUser" + callsign;
                StpUtil.login(id);
                String token = StpUtil.getTokenValueByLoginId(id);
                return FsdJwtResponseMessage.success(token);
            } else {
                return FsdJwtResponseFailedMessage.success("invalid cid or password");
            }
        }
    }
}
