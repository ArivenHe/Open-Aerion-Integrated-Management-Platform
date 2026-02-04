package cn.ariven.openaimpbackend.dto.response.openfsd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeyValuePair {
    private String key;
    private String value;
}
