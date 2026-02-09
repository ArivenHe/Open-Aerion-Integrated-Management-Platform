package cn.ariven.openaimpbackend.dto.request.activity;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestActivityId {
    @NotNull(message = "需要活动ID")
    private Long id;
}
