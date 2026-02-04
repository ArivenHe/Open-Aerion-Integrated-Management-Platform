package cn.ariven.openaimpbackend.dto.request.openfsd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoadUserRequest {
    private Integer cid;
}
