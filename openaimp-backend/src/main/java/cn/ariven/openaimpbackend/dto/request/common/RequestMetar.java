package cn.ariven.openaimpbackend.dto.request.common;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RequestMetar {
    @NotBlank(message = "需要机场ICAO")
    private String icao;//机场ICAO
}
