package cn.ariven.openaimpbackend.service.impl;

import cn.ariven.openaimpbackend.dto.request.openfsd.ConfigUpdateRequest;
import cn.ariven.openaimpbackend.dto.request.openfsd.CreateTokenRequest;
import cn.ariven.openaimpbackend.dto.request.openfsd.CreateUserRequest;
import cn.ariven.openaimpbackend.dto.request.openfsd.FsdJwtRequest;
import cn.ariven.openaimpbackend.dto.request.openfsd.KickUserRequest;
import cn.ariven.openaimpbackend.dto.request.openfsd.LoadUserRequest;
import cn.ariven.openaimpbackend.dto.request.openfsd.LoginRequest;
import cn.ariven.openaimpbackend.dto.request.openfsd.RefreshRequest;
import cn.ariven.openaimpbackend.dto.request.openfsd.UpdateUserRequest;
import cn.ariven.openaimpbackend.dto.response.openfsd.AccessToken;
import cn.ariven.openaimpbackend.dto.response.openfsd.ApiToken;
import cn.ariven.openaimpbackend.dto.response.openfsd.ApiV1Response;
import cn.ariven.openaimpbackend.dto.response.openfsd.AuthTokens;
import cn.ariven.openaimpbackend.dto.response.openfsd.ConfigLoadData;
import cn.ariven.openaimpbackend.dto.response.openfsd.FsdJwt;
import cn.ariven.openaimpbackend.dto.response.openfsd.KeyValuePair;
import cn.ariven.openaimpbackend.dto.response.openfsd.ServerInfo;
import cn.ariven.openaimpbackend.dto.response.openfsd.StatusJson;
import cn.ariven.openaimpbackend.dto.response.openfsd.UserInfo;
import cn.ariven.openaimpbackend.service.OpenFsdApiService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenFsdApiServiceImpl implements OpenFsdApiService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${openfsd.api.base-url}")
    private String baseUrl;

    // 登录获取 access_token 与 refresh_token
    @Override
    public AuthTokens login(Integer cid, String password, boolean rememberMe) {
        LoginRequest request = new LoginRequest(cid, password, rememberMe);
        return postApi("/api/v1/auth/login", request, null, new ParameterizedTypeReference<>() {});
    }

    // 使用 refresh_token 刷新 access_token
    @Override
    public AccessToken refresh(String refreshToken) {
        RefreshRequest request = new RefreshRequest(refreshToken);
        return postApi("/api/v1/auth/refresh", request, null, new ParameterizedTypeReference<>() {});
    }

    // 获取 FSD JWT
    @Override
    public FsdJwt fsdJwt(String cid, String password) {
        FsdJwtRequest request = new FsdJwtRequest(cid, password);
        return postApi("/api/v1/fsd-jwt", request, null, new ParameterizedTypeReference<>() {});
    }

    // 按 CID 查询用户信息
    @Override
    public UserInfo loadUser(int cid, String accessToken) {
        LoadUserRequest request = new LoadUserRequest(cid);
        return postApi("/api/v1/user/load", request, accessToken, new ParameterizedTypeReference<>() {});
    }

    // 更新用户信息
    @Override
    public UserInfo updateUser(UpdateUserRequest request, String accessToken) {
        return patchUserUpdate(request, accessToken);
    }

    // 创建用户
    @Override
    public UserInfo createUser(CreateUserRequest request, String accessToken) {
        return postApi("/api/v1/user/create", request, accessToken, new ParameterizedTypeReference<>() {});
    }

    // 读取配置键值对
    @Override
    public List<KeyValuePair> loadConfig(String accessToken) {
        ConfigLoadData result = getApi("/api/v1/config/load", accessToken, new ParameterizedTypeReference<>() {});
        if (result.getKeyValuePairs() == null) {
            throw new RuntimeException("OpenFSD API 返回为空");
        }
        return result.getKeyValuePairs();
    }

    // 更新配置键值对
    @Override
    public void updateConfig(List<KeyValuePair> keyValuePairs, String accessToken) {
        ConfigUpdateRequest request = new ConfigUpdateRequest(keyValuePairs);
        postApi("/api/v1/config/update", request, accessToken, new ParameterizedTypeReference<>() {});
    }

    // 重置 JWT secret key
    @Override
    public void resetSecretKey(String accessToken) {
        postApi("/api/v1/config/resetsecretkey", null, accessToken, new ParameterizedTypeReference<>() {});
    }

    // 创建 API Token
    @Override
    public ApiToken createApiToken(String expiryDateTime, String accessToken) {
        CreateTokenRequest request = new CreateTokenRequest(expiryDateTime);
        return postApi("/api/v1/config/createtoken", request, accessToken, new ParameterizedTypeReference<>() {});
    }

    // 踢出在线用户
    @Override
    public void kickUser(String callsign, String accessToken) {
        KickUserRequest request = new KickUserRequest(callsign);
        postApi("/api/v1/fsdconn/kickuser", request, accessToken, new ParameterizedTypeReference<>() {});
    }

    // 获取 status.txt
    @Override
    public String getStatusText() {
        return getText("/api/v1/data/status.txt");
    }

    // 获取 status.json
    @Override
    public StatusJson getStatusJson() {
        return getPlain("/api/v1/data/status.json", new ParameterizedTypeReference<>() {});
    }

    // 获取 openfsd-servers.txt
    @Override
    public String getOpenFsdServersText() {
        return getText("/api/v1/data/openfsd-servers.txt");
    }

    // 获取 openfsd-servers.json
    @Override
    public List<ServerInfo> getOpenFsdServersJson() {
        return getPlain("/api/v1/data/openfsd-servers.json", new ParameterizedTypeReference<>() {});
    }

    // 获取 sweatbox-servers.json
    @Override
    public List<ServerInfo> getSweatboxServersJson() {
        return getPlain("/api/v1/data/sweatbox-servers.json", new ParameterizedTypeReference<>() {});
    }

    // 获取 all-servers.json
    @Override
    public List<ServerInfo> getAllServersJson() {
        return getPlain("/api/v1/data/all-servers.json", new ParameterizedTypeReference<>() {});
    }

    // 获取 openfsd-data.json
    @Override
    public Map<String, Object> getOpenFsdData() {
        return getPlain("/api/v1/data/openfsd-data.json", new ParameterizedTypeReference<>() {});
    }

    private <T> T postApi(String path, Object body, String accessToken, ParameterizedTypeReference<ApiV1Response<T>> typeRef) {
        HttpEntity<Object> entity = new HttpEntity<>(body, createHeaders(accessToken));
        try {
            ResponseEntity<ApiV1Response<T>> response = restTemplate.exchange(buildUrl(path), HttpMethod.POST, entity, typeRef);
            return unwrapApiResponse(response.getBody());
        } catch (RestClientResponseException e) {
            throw new RuntimeException(e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private <T> T getApi(String path, String accessToken, ParameterizedTypeReference<ApiV1Response<T>> typeRef) {
        HttpEntity<Object> entity = new HttpEntity<>(createHeaders(accessToken));
        try {
            ResponseEntity<ApiV1Response<T>> response = restTemplate.exchange(buildUrl(path), HttpMethod.GET, entity, typeRef);
            return unwrapApiResponse(response.getBody());
        } catch (RestClientResponseException e) {
            throw new RuntimeException(e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private <T> T getPlain(String path, ParameterizedTypeReference<T> typeRef) {
        try {
            ResponseEntity<T> response = restTemplate.exchange(buildUrl(path), HttpMethod.GET, null, typeRef);
            return response.getBody();
        } catch (RestClientResponseException e) {
            throw new RuntimeException(e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private String getText(String path) {
        try {
            ResponseEntity<String> response = restTemplate.exchange(buildUrl(path), HttpMethod.GET, null, String.class);
            return response.getBody();
        } catch (RestClientResponseException e) {
            throw new RuntimeException(e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private <T> T unwrapApiResponse(ApiV1Response<T> response) {
        if (response == null) {
            throw new RuntimeException("OpenFSD API 返回为空");
        }
        if (response.getErr() != null) {
            throw new RuntimeException(response.getErr());
        }
        return response.getData();
    }

    private UserInfo patchUserUpdate(UpdateUserRequest request, String accessToken) {
        try {
            String body = objectMapper.writeValueAsString(request);
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(buildUrl("/api/v1/user/update")))
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/json");
            if (accessToken != null && !accessToken.isBlank()) {
                builder.header("Authorization", "Bearer " + accessToken);
            }
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                ApiV1Response<UserInfo> apiResponse = objectMapper.readValue(
                        response.body(),
                        new TypeReference<ApiV1Response<UserInfo>>() {}
                );
                return unwrapApiResponse(apiResponse);
            }
            throw new RuntimeException(response.body());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private HttpHeaders createHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (accessToken != null && !accessToken.isBlank()) {
            headers.setBearerAuth(accessToken);
        }
        return headers;
    }

    private String buildUrl(String path) {
        if (baseUrl.endsWith("/") && path.startsWith("/")) {
            return baseUrl.substring(0, baseUrl.length() - 1) + path;
        }
        if (!baseUrl.endsWith("/") && !path.startsWith("/")) {
            return baseUrl + "/" + path;
        }
        return baseUrl + path;
    }

}
