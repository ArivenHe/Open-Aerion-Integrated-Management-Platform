package cn.ariven.openaimpbackend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseFsdApiEnvelope<T> {
  private String version;

  @JsonProperty("err")
  private String error;

  private T data;
}
