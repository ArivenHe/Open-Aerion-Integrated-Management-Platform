package cn.ariven.openaimpbackend.service;

import cn.ariven.openaimpbackend.dto.request.openfsd.CreateUserRequest;
import cn.ariven.openaimpbackend.dto.request.openfsd.UpdateUserRequest;
import cn.ariven.openaimpbackend.dto.response.openfsd.AccessToken;
import cn.ariven.openaimpbackend.dto.response.openfsd.ApiToken;
import cn.ariven.openaimpbackend.dto.response.openfsd.AuthTokens;
import cn.ariven.openaimpbackend.dto.response.openfsd.FsdJwt;
import cn.ariven.openaimpbackend.dto.response.openfsd.KeyValuePair;
import cn.ariven.openaimpbackend.dto.response.openfsd.ServerInfo;
import cn.ariven.openaimpbackend.dto.response.openfsd.StatusJson;
import cn.ariven.openaimpbackend.dto.response.openfsd.UserInfo;

import java.util.List;
import java.util.Map;

public interface OpenFsdApiService {

    // 登录获取 access_token 与 refresh_token
    AuthTokens login(Integer cid, String password, boolean rememberMe);

    // 使用 refresh_token 刷新 access_token
    AccessToken refresh(String refreshToken);

    // 获取 FSD JWT
    FsdJwt fsdJwt(String cid, String password);

    // 按 CID 查询用户信息
    UserInfo loadUser(int cid, String accessToken);

    // 更新用户信息
    UserInfo updateUser(UpdateUserRequest request, String accessToken);

    // 创建用户
    UserInfo createUser(CreateUserRequest request, String accessToken);

    // 读取配置键值对
    List<KeyValuePair> loadConfig(String accessToken);

    // 更新配置键值对
    void updateConfig(List<KeyValuePair> keyValuePairs, String accessToken);

    // 重置 JWT secret key
    void resetSecretKey(String accessToken);

    // 创建 API Token
    ApiToken createApiToken(String expiryDateTime, String accessToken);

    // 踢出在线用户
    void kickUser(String callsign, String accessToken);

    // 获取 status.txt
    String getStatusText();

    // 获取 status.json
    StatusJson getStatusJson();

    // 获取 openfsd-servers.txt
    String getOpenFsdServersText();

    // 获取 openfsd-servers.json
    List<ServerInfo> getOpenFsdServersJson();

    // 获取 sweatbox-servers.json
    List<ServerInfo> getSweatboxServersJson();

    // 获取 all-servers.json
    List<ServerInfo> getAllServersJson();

    // 获取 openfsd-data.json
    Map<String, Object> getOpenFsdData();
}
