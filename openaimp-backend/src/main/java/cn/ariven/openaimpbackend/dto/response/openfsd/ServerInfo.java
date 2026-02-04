package cn.ariven.openaimpbackend.dto.response.openfsd;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServerInfo {
    private String ident;
    @JsonProperty("hostname_or_ip")
    private String hostnameOrIp;
    private String location;
    private String name;
    @JsonProperty("clients_connection_allowed")
    private Integer clientsConnectionAllowed;
    @JsonProperty("client_connections_allowed")
    private Boolean clientConnectionsAllowed;
    @JsonProperty("is_sweatbox")
    private Boolean isSweatbox;
}
