package cn.ariven.openaimpbackend.dto.response.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseAtisInfo {
    private String code;
    private String cn;
    private String en;
}
