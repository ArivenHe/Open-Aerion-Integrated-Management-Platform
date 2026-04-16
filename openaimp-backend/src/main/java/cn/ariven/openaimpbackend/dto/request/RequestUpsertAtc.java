package cn.ariven.openaimpbackend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestUpsertAtc {
  private Integer userId;
  private Integer networkRating;
  private Integer facilityType;
  private Boolean enabled;
  private String remarks;
}
