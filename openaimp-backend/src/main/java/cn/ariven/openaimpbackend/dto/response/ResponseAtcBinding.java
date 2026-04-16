package cn.ariven.openaimpbackend.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseAtcBinding {
  private Long id;
  private Integer userId;
  private String email;
  private Integer networkRating;
  private String networkRatingName;
  private Integer facilityType;
  private String facilityTypeName;
  private Boolean enabled;
  private String remarks;
  private List<FacilityPermissionView> eligibleFacilities;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class FacilityPermissionView {
    private Integer code;
    private String name;
    private Boolean allowed;
  }
}
