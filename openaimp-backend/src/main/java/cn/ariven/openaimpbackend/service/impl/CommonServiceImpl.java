package cn.ariven.openaimpbackend.service.impl;

import cn.ariven.openaimpbackend.dto.response.openfsd.AuthTokens;
import cn.ariven.openaimpbackend.service.CommonService;
import cn.ariven.openaimpbackend.service.OpenFsdApiService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonServiceImpl implements CommonService {
    private final OpenFsdApiService openFsdApiService;

    @Value("${openfsd.admin.cid}")
    private Integer adminCid;

    @Value("${openfsd.admin.password}")
    private String adminPassword;

    @Override
    public String getFsdAccessTokenBySystemAdmin() {
        AuthTokens authTokens = openFsdApiService.login(adminCid, adminPassword, false);
        log.info(authTokens.toString());
        return authTokens.getAccessToken();
    }


}
