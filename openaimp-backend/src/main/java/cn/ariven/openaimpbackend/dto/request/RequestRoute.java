package cn.ariven.openaimpbackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RequestRoute {
    @NotBlank(message = "需要起飞机场ICAO")
    private String depIcao;
    @NotBlank(message = "需要落地机场ICAO")
    private String arrICao;
}
