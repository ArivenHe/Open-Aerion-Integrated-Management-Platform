package cn.ariven.openaimpbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PilotRankingDTO {
    private int cid;
    private String callsign;
    private long totalOnlineTimeInSeconds;
    private double totalOnlineTimeInHours;

    public PilotRankingDTO(int cid, String callsign, long totalOnlineTimeInSeconds) {
        this.cid = cid;
        this.callsign = callsign;
        this.totalOnlineTimeInSeconds = totalOnlineTimeInSeconds;
        this.totalOnlineTimeInHours = secondsToHours(totalOnlineTimeInSeconds);
    }

    private double secondsToHours(long seconds) {
        return Math.round((seconds / 3600.0) * 100.0) / 100.0;
    }
}
