package cn.ariven.openaimpbackend.dto.response.activity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseActivityUser {
    private Long id;
    private String callsign;
}
