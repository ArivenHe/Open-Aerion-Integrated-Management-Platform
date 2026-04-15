package cn.ariven.openaimpbackend.service.impl;

import cn.ariven.openaimpbackend.config.FsdProperties;
import cn.ariven.openaimpbackend.constant.FsdConstants;
import cn.ariven.openaimpbackend.dto.request.RequestFsdCreateUser;
import cn.ariven.openaimpbackend.dto.request.RequestFsdIssueToken;
import cn.ariven.openaimpbackend.dto.response.ResponseFsdApiEnvelope;
import cn.ariven.openaimpbackend.dto.response.ResponseFsdIssueToken;
import cn.ariven.openaimpbackend.dto.response.ResponseFsdOnlineUsers;
import cn.ariven.openaimpbackend.dto.response.ResponseFsdTokenClaims;
import cn.ariven.openaimpbackend.dto.response.ResponseFsdUser;
import cn.ariven.openaimpbackend.service.FsdService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class FsdServiceImpl implements FsdService {
  private static final Base64.Encoder BASE64_URL_ENCODER =
      Base64.getUrlEncoder().withoutPadding();
  private static final Base64.Decoder BASE64_URL_DECODER = Base64.getUrlDecoder();

  private final FsdProperties fsdProperties;
  private final ObjectMapper objectMapper;

  private HttpClient httpClient;

  @PostConstruct
  void init() {
    httpClient =
        HttpClient.newBuilder()
            .connectTimeout(
                Duration.ofSeconds(resolvePositive(fsdProperties.getRequestTimeoutSeconds(), 10L)))
            .build();
  }

  @Override
  public ResponseFsdIssueToken issueToken(RequestFsdIssueToken requestFsdIssueToken) {
    if (requestFsdIssueToken == null) {
      throw new IllegalArgumentException("FSD token 请求不能为空");
    }

    String tokenType = normalizeRequired(requestFsdIssueToken.getTokenType(), "tokenType");
    Integer cid = requirePositive(requestFsdIssueToken.getCid(), "cid");
    Integer networkRating =
        requireNonNull(requestFsdIssueToken.getNetworkRating(), "networkRating");
    long validitySeconds = resolvePositive(requestFsdIssueToken.getValiditySeconds(), 300L);
    Instant expiresAt = Instant.now().plusSeconds(validitySeconds);

    String token =
        signToken(
            tokenType,
            cid,
            normalizeNullable(requestFsdIssueToken.getFirstName()),
            normalizeNullable(requestFsdIssueToken.getLastName()),
            networkRating,
            Duration.ofSeconds(validitySeconds));

    return ResponseFsdIssueToken.builder()
        .token(token)
        .tokenType(tokenType)
        .cid(cid)
        .networkRating(networkRating)
        .expiresAt(expiresAt)
        .build();
  }

  @Override
  public ResponseFsdIssueToken issueServiceToken() {
    Integer cid = requirePositive(fsdProperties.getServiceCid(), "app.fsd.service-cid");
    Integer networkRating =
        requireNonNull(fsdProperties.getServiceNetworkRating(), "app.fsd.service-network-rating");
    if (networkRating < FsdConstants.NETWORK_RATING_ADMINISTRATOR) {
      throw new IllegalArgumentException("FSD 服务令牌的 network rating 必须至少为管理员级别");
    }

    long validitySeconds = resolvePositive(fsdProperties.getServiceTokenValiditySeconds(), 900L);
    return issueToken(
        RequestFsdIssueToken.builder()
            .tokenType(FsdConstants.TOKEN_TYPE_FSD_SERVICE)
            .cid(cid)
            .firstName(fsdProperties.getServiceFirstName())
            .lastName(fsdProperties.getServiceLastName())
            .networkRating(networkRating)
            .validitySeconds(validitySeconds)
            .build());
  }

  @Override
  public ResponseFsdIssueToken issueAccessToken() {
    Integer cid = requirePositive(fsdProperties.getServiceCid(), "app.fsd.service-cid");
    Integer networkRating =
        requireNonNull(fsdProperties.getServiceNetworkRating(), "app.fsd.service-network-rating");
    if (networkRating < FsdConstants.NETWORK_RATING_ADMINISTRATOR) {
      throw new IllegalArgumentException("FSD access token 的 network rating 必须至少为管理员级别");
    }

    long validitySeconds = resolvePositive(fsdProperties.getServiceTokenValiditySeconds(), 900L);
    return issueToken(
        RequestFsdIssueToken.builder()
            .tokenType(FsdConstants.TOKEN_TYPE_ACCESS)
            .cid(cid)
            .firstName(fsdProperties.getServiceFirstName())
            .lastName(fsdProperties.getServiceLastName())
            .networkRating(networkRating)
            .validitySeconds(validitySeconds)
            .build());
  }

  @Override
  public ResponseFsdTokenClaims parseToken(String token) {
    String rawToken = normalizeRequired(token, "token");
    String[] parts = rawToken.split("\\.");
    if (parts.length != 3) {
      throw new IllegalArgumentException("FSD JWT 格式非法");
    }

    JsonNode header = decodeJson(parts[0], "JWT header");
    if (!"HS256".equals(header.path("alg").asText())) {
      throw new IllegalArgumentException("FSD JWT 仅支持 HS256 算法");
    }

    byte[] expectedSignature = sign(parts[0] + "." + parts[1]);
    byte[] actualSignature = decodeBase64Url(parts[2], "JWT signature");
    if (!MessageDigest.isEqual(expectedSignature, actualSignature)) {
      throw new IllegalArgumentException("FSD JWT 签名校验失败");
    }

    JsonNode payload = decodeJson(parts[1], "JWT payload");
    validateRegisteredClaims(payload);

    return ResponseFsdTokenClaims.builder()
        .issuer(payload.path("iss").asText(null))
        .tokenType(textOrNull(payload, "token_type"))
        .cid(intOrNull(payload, "cid"))
        .firstName(textOrNull(payload, "first_name"))
        .lastName(textOrNull(payload, "last_name"))
        .networkRating(intOrNull(payload, "network_rating"))
        .issuedAt(instantOrNull(payload, "iat"))
        .notBefore(instantOrNull(payload, "nbf"))
        .expiresAt(instantOrNull(payload, "exp"))
        .jwtId(textOrNull(payload, "jti"))
        .build();
  }

  @Override
  public ResponseFsdOnlineUsers getOnlineUsers() {
    HttpRequest request =
        authorizedRequestBuilder("/online_users")
            .GET()
            .header("Accept", "application/json")
            .build();

    HttpResponse<String> response = send(request);
    if (response.statusCode() != 200) {
      throw new IllegalStateException("获取 FSD 在线用户失败，HTTP 状态码: " + response.statusCode());
    }

    try {
      ResponseFsdOnlineUsers onlineUsers =
          objectMapper.readValue(response.body(), ResponseFsdOnlineUsers.class);
      if (onlineUsers.getPilots() == null) {
        onlineUsers.setPilots(java.util.List.of());
      }
      if (onlineUsers.getAtc() == null) {
        onlineUsers.setAtc(java.util.List.of());
      }
      return onlineUsers;
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("解析 FSD 在线用户响应失败", e);
    }
  }

  @Override
  public ResponseFsdUser createUser(RequestFsdCreateUser requestFsdCreateUser) {
    if (requestFsdCreateUser == null) {
      throw new IllegalArgumentException("FSD 用户创建请求不能为空");
    }

    String password = normalizeRequired(requestFsdCreateUser.getPassword(), "password");
    if (password.length() < 8) {
      throw new IllegalArgumentException("FSD 用户密码长度不能小于 8 位");
    }
    if (password.contains(":")) {
      throw new IllegalArgumentException("FSD 用户密码不能包含冒号字符");
    }

    Integer networkRating =
        requestFsdCreateUser.getNetworkRating() != null
            ? requestFsdCreateUser.getNetworkRating()
            : requireNonNull(
                fsdProperties.getDefaultUserNetworkRating(), "app.fsd.default-user-network-rating");

    Map<String, Object> payload = new LinkedHashMap<>();
    payload.put("password", password);
    payload.put("network_rating", networkRating);
    if (StringUtils.hasText(requestFsdCreateUser.getFirstName())) {
      payload.put("first_name", requestFsdCreateUser.getFirstName().trim());
    }
    if (StringUtils.hasText(requestFsdCreateUser.getLastName())) {
      payload.put("last_name", requestFsdCreateUser.getLastName().trim());
    }

    String requestBody;
    try {
      requestBody = objectMapper.writeValueAsString(payload);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("序列化 FSD 创建用户请求失败", e);
    }

    HttpRequest request =
        webAuthorizedRequestBuilder("/api/v1/user/create")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .build();

    HttpResponse<String> response = send(request);
    if (response.statusCode() != 201) {
      throw new IllegalStateException("FSD 创建用户失败，HTTP 状态码: " + response.statusCode());
    }

    try {
      ResponseFsdApiEnvelope<ResponseFsdUser> envelope =
          objectMapper.readValue(
              response.body(), new TypeReference<ResponseFsdApiEnvelope<ResponseFsdUser>>() {});
      if (envelope.getError() != null) {
        throw new IllegalStateException("FSD 创建用户失败: " + envelope.getError());
      }
      if (envelope.getData() == null || envelope.getData().getCid() == null) {
        throw new IllegalStateException("FSD 创建用户成功但未返回有效 CID");
      }
      return envelope.getData();
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("解析 FSD 创建用户响应失败", e);
    }
  }

  @Override
  public void kickUser(String callsign) {
    String normalizedCallsign = normalizeRequired(callsign, "callsign").toUpperCase();

    if (!isValidClientCallsign(normalizedCallsign)) {
      throw new IllegalArgumentException("无效的 FSD callsign: " + normalizedCallsign);
    }

    String requestBody;
    try {
      requestBody = objectMapper.writeValueAsString(Map.of("callsign", normalizedCallsign));
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("序列化 FSD 踢人请求失败", e);
    }

    HttpRequest request =
        authorizedRequestBuilder("/kick_user")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
            .header("Content-Type", "application/json")
            .build();

    HttpResponse<String> response = send(request);
    if (response.statusCode() == 204) {
      return;
    }
    if (response.statusCode() == 404) {
      throw new IllegalArgumentException("目标 callsign 不在线: " + normalizedCallsign);
    }

    throw new IllegalStateException("踢出 FSD 在线用户失败，HTTP 状态码: " + response.statusCode());
  }

  @Override
  public boolean isValidClientCallsign(String callsign) {
    if (!StringUtils.hasText(callsign)) {
      return false;
    }

    String normalized = callsign.trim();
    if (normalized.length() < 2 || normalized.length() > 10) {
      return false;
    }
    if (FsdConstants.RESERVED_CALLSIGNS.contains(normalized)) {
      return false;
    }

    for (int i = 0; i < normalized.length(); i++) {
      char value = normalized.charAt(i);
      boolean validUpper = value >= 'A' && value <= 'Z';
      boolean validDigit = value >= '0' && value <= '9';
      boolean validExtra = value == '-' || value == '_';
      if (!(validUpper || validDigit || validExtra)) {
        return false;
      }
    }

    return true;
  }

  @Override
  public boolean isAllowedFacilityType(Integer networkRating, Integer facilityType) {
    if (networkRating == null || facilityType == null) {
      return false;
    }
    if (facilityType == 0) {
      return true;
    }

    Integer minRating =
        switch (facilityType) {
          case 1 -> FsdConstants.NETWORK_RATING_CONTROLLER_1;
          case 2, 3 -> FsdConstants.NETWORK_RATING_STUDENT_1;
          case 4 -> FsdConstants.NETWORK_RATING_STUDENT_2;
          case 5 -> FsdConstants.NETWORK_RATING_STUDENT_3;
          case 6 -> FsdConstants.NETWORK_RATING_CONTROLLER_1;
          default -> null;
        };

    return minRating != null && networkRating >= minRating;
  }

  @Override
  public boolean isSupportedProtocolRevision(Integer protocolRevision) {
    if (protocolRevision == null) {
      return false;
    }
    return protocolRevision >= FsdConstants.PROTOCOL_REVISION_MIN
        && protocolRevision <= FsdConstants.PROTOCOL_REVISION_MAX;
  }

  private String signToken(
      String tokenType,
      Integer cid,
      String firstName,
      String lastName,
      Integer networkRating,
      Duration validityDuration) {
    ensureJwtSecretConfigured();

    Instant now = Instant.now();
    Instant expiresAt = now.plus(validityDuration);

    Map<String, Object> header = new LinkedHashMap<>();
    header.put("alg", "HS256");
    header.put("typ", "JWT");

    Map<String, Object> payload = new LinkedHashMap<>();
    payload.put("iss", FsdConstants.JWT_ISSUER);
    payload.put("exp", expiresAt.getEpochSecond());
    payload.put("nbf", now.minusSeconds(30).getEpochSecond());
    payload.put("iat", now.getEpochSecond());
    payload.put("jti", UUID.randomUUID().toString());
    payload.put("token_type", tokenType);
    payload.put("cid", cid);
    if (StringUtils.hasText(firstName)) {
      payload.put("first_name", firstName);
    }
    if (StringUtils.hasText(lastName)) {
      payload.put("last_name", lastName);
    }
    payload.put("network_rating", networkRating);

    String headerPart = encodeJson(header);
    String payloadPart = encodeJson(payload);
    String signingInput = headerPart + "." + payloadPart;
    String signaturePart = BASE64_URL_ENCODER.encodeToString(sign(signingInput));
    return signingInput + "." + signaturePart;
  }

  private HttpRequest.Builder authorizedRequestBuilder(String path) {
    return HttpRequest.newBuilder(buildUri(path))
        .timeout(Duration.ofSeconds(resolvePositive(fsdProperties.getRequestTimeoutSeconds(), 10L)))
        .header("Authorization", "Bearer " + issueServiceToken().getToken());
  }

  private HttpRequest.Builder webAuthorizedRequestBuilder(String path) {
    return HttpRequest.newBuilder(buildWebUri(path))
        .timeout(Duration.ofSeconds(resolvePositive(fsdProperties.getRequestTimeoutSeconds(), 10L)))
        .header("Authorization", "Bearer " + issueAccessToken().getToken());
  }

  private URI buildUri(String path) {
    String baseUrl =
        normalizeRequired(fsdProperties.getServiceBaseUrl(), "app.fsd.service-base-url");
    String normalizedPath = path.startsWith("/") ? path : "/" + path;
    if (baseUrl.endsWith("/")) {
      return URI.create(baseUrl.substring(0, baseUrl.length() - 1) + normalizedPath);
    }
    return URI.create(baseUrl + normalizedPath);
  }

  private URI buildWebUri(String path) {
    String baseUrl = normalizeRequired(fsdProperties.getWebBaseUrl(), "app.fsd.web-base-url");
    String normalizedPath = path.startsWith("/") ? path : "/" + path;
    if (baseUrl.endsWith("/")) {
      return URI.create(baseUrl.substring(0, baseUrl.length() - 1) + normalizedPath);
    }
    return URI.create(baseUrl + normalizedPath);
  }

  private HttpResponse<String> send(HttpRequest request) {
    try {
      return httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    } catch (Exception e) {
      throw new IllegalStateException("调用 FSD 服务失败: " + request.uri(), e);
    }
  }

  private void validateRegisteredClaims(JsonNode payload) {
    if (!FsdConstants.JWT_ISSUER.equals(payload.path("iss").asText())) {
      throw new IllegalArgumentException("FSD JWT issuer 非法");
    }

    Instant now = Instant.now();
    Instant expiresAt = instantOrNull(payload, "exp");
    if (expiresAt == null || !expiresAt.isAfter(now)) {
      throw new IllegalArgumentException("FSD JWT 已过期");
    }

    Instant notBefore = instantOrNull(payload, "nbf");
    if (notBefore != null && notBefore.isAfter(now)) {
      throw new IllegalArgumentException("FSD JWT 尚未生效");
    }
  }

  private JsonNode decodeJson(String base64Value, String partName) {
    try {
      return objectMapper.readTree(decodeBase64Url(base64Value, partName));
    } catch (Exception e) {
      throw new IllegalArgumentException("解析 " + partName + " 失败", e);
    }
  }

  private byte[] decodeBase64Url(String value, String partName) {
    try {
      return BASE64_URL_DECODER.decode(value);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Base64URL 解码失败: " + partName, e);
    }
  }

  private String encodeJson(Map<String, Object> value) {
    try {
      return BASE64_URL_ENCODER.encodeToString(objectMapper.writeValueAsBytes(value));
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("序列化 FSD JWT 失败", e);
    }
  }

  private byte[] sign(String value) {
    ensureJwtSecretConfigured();
    try {
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(
          new SecretKeySpec(
              fsdProperties.getJwtSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
      return mac.doFinal(value.getBytes(StandardCharsets.UTF_8));
    } catch (GeneralSecurityException e) {
      throw new IllegalStateException("签名 FSD JWT 失败", e);
    }
  }

  private void ensureJwtSecretConfigured() {
    if (!StringUtils.hasText(fsdProperties.getJwtSecret())) {
      throw new IllegalStateException("未配置 app.fsd.jwt-secret，无法生成或校验 FSD JWT");
    }
  }

  private String normalizeRequired(String value, String fieldName) {
    if (!StringUtils.hasText(value)) {
      throw new IllegalArgumentException(fieldName + " 不能为空");
    }
    return value.trim();
  }

  private String normalizeNullable(String value) {
    return StringUtils.hasText(value) ? value.trim() : null;
  }

  private Integer requirePositive(Integer value, String fieldName) {
    if (value == null || value <= 0) {
      throw new IllegalArgumentException(fieldName + " 必须为正整数");
    }
    return value;
  }

  private Integer requireNonNull(Integer value, String fieldName) {
    if (value == null) {
      throw new IllegalArgumentException(fieldName + " 不能为空");
    }
    return value;
  }

  private long resolvePositive(Long value, long defaultValue) {
    if (value == null) {
      return defaultValue;
    }
    if (value <= 0) {
      throw new IllegalArgumentException("配置值必须大于 0");
    }
    return value;
  }

  private String textOrNull(JsonNode payload, String fieldName) {
    JsonNode node = payload.get(fieldName);
    if (node == null || node.isNull()) {
      return null;
    }
    return node.asText();
  }

  private Integer intOrNull(JsonNode payload, String fieldName) {
    JsonNode node = payload.get(fieldName);
    if (node == null || node.isNull()) {
      return null;
    }
    return node.asInt();
  }

  private Instant instantOrNull(JsonNode payload, String fieldName) {
    JsonNode node = payload.get(fieldName);
    if (node == null || node.isNull() || !node.canConvertToLong()) {
      return null;
    }
    return Instant.ofEpochSecond(node.asLong());
  }
}
