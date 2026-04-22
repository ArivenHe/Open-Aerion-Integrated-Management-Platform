package cn.ariven.openaimpbackend.dto.response.global;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseString {
  private Boolean isSuccess;
  private String message;
}
