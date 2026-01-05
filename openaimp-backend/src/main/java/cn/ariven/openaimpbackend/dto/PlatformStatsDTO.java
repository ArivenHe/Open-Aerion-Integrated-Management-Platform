package cn.ariven.openaimpbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlatformStatsDTO {
    private int userCount;
    private int atcCount;
    private int activityCount;
}
