package cn.ariven.openaimpbackend.dto.response.openfsd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiV1Response<T> {
    private String version;
    private String err;
    private T data;
}
