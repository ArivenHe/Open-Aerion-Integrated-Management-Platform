package cn.ariven.openaimpbackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RequestAtisInfo {
    @NotBlank(message = "需要机场ICAO")
    private String icao;
}
