package cn.ariven.openaimpbackend.dto.request.openfsd;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTokenRequest {
    @JsonProperty("expiry_date_time")
    private String expiryDateTime;
}
