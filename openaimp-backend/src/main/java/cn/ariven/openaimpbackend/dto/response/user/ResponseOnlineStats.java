package cn.ariven.openaimpbackend.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseOnlineStats {
    private Long onlineCount;
    private List<OnlineUserItem> onlineUsers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OnlineUserItem {
        private Long userId;
        private String callsign;
        private LocalDateTime lastLoginTime;
        private Long onlineDurationSeconds;
    }
}
