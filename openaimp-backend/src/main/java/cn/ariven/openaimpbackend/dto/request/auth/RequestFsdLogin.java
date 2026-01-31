package cn.ariven.openaimpbackend.dto.request.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestFsdLogin {
    @NotBlank(message = "CID is required")
    private String cid;

    @NotBlank(message = "Password is required")
    private String password;

    @NotNull(message = "Network rating is required")
    @JsonProperty("network_rating")
    private Integer networkRating;
}
