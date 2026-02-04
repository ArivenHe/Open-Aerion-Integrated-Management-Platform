package cn.ariven.openaimpbackend.dto.response.openfsd;

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
public class StatusData {
    private List<String> v3;
    private List<String> servers;
    @JsonProperty("servers_sweatbox")
    private List<String> serversSweatbox;
    @JsonProperty("servers_all")
    private List<String> serversAll;
}
