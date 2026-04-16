package cn.ariven.openaimpbackend.service.impl;

import cn.ariven.openaimpbackend.constant.FsdConstants;
import cn.ariven.openaimpbackend.constant.RbacConstants;
import cn.ariven.openaimpbackend.dto.request.RequestFsdUpdateUser;
import cn.ariven.openaimpbackend.dto.request.RequestUpsertAtc;
import cn.ariven.openaimpbackend.dto.response.ResponseAtcBinding;
import cn.ariven.openaimpbackend.mapper.AtcMapper;
import cn.ariven.openaimpbackend.mapper.AuthMapper;
import cn.ariven.openaimpbackend.pojo.Atc;
import cn.ariven.openaimpbackend.pojo.Auth;
import cn.ariven.openaimpbackend.service.AtcService;
import cn.ariven.openaimpbackend.service.FsdService;
import cn.ariven.openaimpbackend.service.RbacService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AtcServiceImpl implements AtcService {
  private final AtcMapper atcMapper;
  private final AuthMapper authMapper;
  private final FsdService fsdService;
  private final RbacService rbacService;

  @Override
  public List<ResponseAtcBinding> listBindings() {
    return atcMapper.findAllByOrderByUserIdAsc().stream().map(this::toResponse).toList();
  }

  @Override
  public ResponseAtcBinding getBinding(Integer userId) {
    return toResponse(getBindingEntity(userId));
  }

  @Override
  @Transactional
  public ResponseAtcBinding upsertBinding(RequestUpsertAtc requestUpsertAtc) {
    if (requestUpsertAtc == null) {
      throw new IllegalArgumentException("ATC 绑定请求不能为空");
    }

    Integer userId = requirePositive(requestUpsertAtc.getUserId(), "userId");
    Integer networkRating =
        requestUpsertAtc.getNetworkRating() != null
            ? requireNetworkRating(requestUpsertAtc.getNetworkRating())
            : defaultNetworkRating(userId);
    Integer facilityType = requireFacilityType(requestUpsertAtc.getFacilityType());
    Boolean enabled =
        requestUpsertAtc.getEnabled() == null ? Boolean.TRUE : requestUpsertAtc.getEnabled();

    Auth auth =
        authMapper
            .findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + userId));

    if (!fsdService.isAllowedFacilityType(networkRating, facilityType)) {
      throw new IllegalArgumentException(
          "当前 FSD network rating 不允许绑定该 facility type: " + facilityType);
    }

    Atc atc =
        atcMapper
            .findByUserId(userId)
            .orElseGet(() -> Atc.builder().userId(userId).build());
    atc.setNetworkRating(networkRating);
    atc.setFacilityType(facilityType);
    atc.setEnabled(enabled);
    atc.setRemarks(normalizeNullable(requestUpsertAtc.getRemarks()));

    Atc saved = atcMapper.save(atc);

    // Keep FSD-side network rating aligned with local ATC binding.
    fsdService.updateUser(
        RequestFsdUpdateUser.builder().cid(auth.getCid()).networkRating(networkRating).build());

    return toResponse(saved);
  }

  @Override
  @Transactional
  public void deleteBinding(Integer userId) {
    getBindingEntity(userId);
    atcMapper.deleteByUserId(userId);
  }

  private Atc getBindingEntity(Integer userId) {
    requirePositive(userId, "userId");
    return atcMapper
        .findByUserId(userId)
        .orElseThrow(() -> new IllegalArgumentException("ATC 绑定不存在: " + userId));
  }

  private ResponseAtcBinding toResponse(Atc atc) {
    Auth auth =
        authMapper
            .findById(atc.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + atc.getUserId()));

    List<ResponseAtcBinding.FacilityPermissionView> eligibleFacilities =
        List.of(0, 1, 2, 3, 4, 5, 6).stream()
            .map(
                code ->
                    ResponseAtcBinding.FacilityPermissionView.builder()
                        .code(code)
                        .name(facilityTypeName(code))
                        .allowed(fsdService.isAllowedFacilityType(atc.getNetworkRating(), code))
                        .build())
            .toList();

    return ResponseAtcBinding.builder()
        .id(atc.getId())
        .userId(atc.getUserId())
        .email(auth.getEmail())
        .networkRating(atc.getNetworkRating())
        .networkRatingName(networkRatingName(atc.getNetworkRating()))
        .facilityType(atc.getFacilityType())
        .facilityTypeName(facilityTypeName(atc.getFacilityType()))
        .enabled(atc.getEnabled())
        .remarks(atc.getRemarks())
        .eligibleFacilities(eligibleFacilities)
        .build();
  }

  private Integer defaultNetworkRating(Integer userId) {
    return rbacService.getRoleCodesByUserId(userId).contains(RbacConstants.ROLE_SUPER_ADMIN)
        ? FsdConstants.NETWORK_RATING_ADMINISTRATOR
        : FsdConstants.NETWORK_RATING_OBSERVER;
  }

  private Integer requirePositive(Integer value, String fieldName) {
    if (value == null || value <= 0) {
      throw new IllegalArgumentException(fieldName + " 必须为正整数");
    }
    return value;
  }

  private Integer requireNetworkRating(Integer networkRating) {
    if (networkRating == null || networkRating < -1 || networkRating > 12) {
      throw new IllegalArgumentException("networkRating 必须在 -1 到 12 之间");
    }
    return networkRating;
  }

  private Integer requireFacilityType(Integer facilityType) {
    if (facilityType == null || facilityType < 0 || facilityType > 6) {
      throw new IllegalArgumentException("facilityType 必须在 0 到 6 之间");
    }
    return facilityType;
  }

  private String normalizeNullable(String value) {
    if (value == null) {
      return null;
    }
    String normalized = value.trim();
    return normalized.isEmpty() ? null : normalized;
  }

  private String networkRatingName(Integer networkRating) {
    return switch (networkRating) {
      case -1 -> "Inactive";
      case 0 -> "Suspended";
      case 1 -> "Observer";
      case 2 -> "Student 1";
      case 3 -> "Student 2";
      case 4 -> "Student 3";
      case 5 -> "Controller 1";
      case 6 -> "Controller 2";
      case 7 -> "Controller 3";
      case 8 -> "Instructor 1";
      case 9 -> "Instructor 2";
      case 10 -> "Instructor 3";
      case 11 -> "Supervisor";
      case 12 -> "Administrator";
      default -> "Unknown";
    };
  }

  private String facilityTypeName(Integer facilityType) {
    return switch (facilityType) {
      case 0 -> "Observer";
      case 1 -> "FSS";
      case 2 -> "DEL";
      case 3 -> "GND";
      case 4 -> "TWR";
      case 5 -> "APP";
      case 6 -> "CTR";
      default -> "Unknown";
    };
  }
}
