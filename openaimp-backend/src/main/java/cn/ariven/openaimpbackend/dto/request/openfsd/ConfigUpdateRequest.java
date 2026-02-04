package cn.ariven.openaimpbackend.dto.request.openfsd;

import cn.ariven.openaimpbackend.dto.response.openfsd.KeyValuePair;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigUpdateRequest {
    @JsonProperty("key_value_pairs")
    private List<KeyValuePair> keyValuePairs;
}
