package cn.ariven.openaimpbackend.service.impl;

import cn.ariven.openaimpbackend.entity.PlatformConfig;
import cn.ariven.openaimpbackend.repository.PlatformConfigRepository;
import cn.ariven.openaimpbackend.service.IStartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StartServiceImpl implements IStartService {
    private final PlatformConfigRepository platformConfigRepository;

    @Override
    public Boolean isFirstInstall() {
        return platformConfigRepository.findById(1).map(config -> config.getFirstInstall() == 1).orElse(false);
    }

    @Override
    public Boolean changeFirstInstall() {
        return platformConfigRepository.findById(1).map(config -> {
            config.setFirstInstall(0);
            platformConfigRepository.save(config);
            return true;
        }).orElse(false);
    }
}
