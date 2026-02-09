package cn.ariven.openaimpbackend.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AirportRating {
    RATING_4C(1, "机场最大支持的飞机：A320、B737"),
    RATING_4D(2, "机场最大支持的飞机：B767、A310"),
    RATING_4E(3, "机场最大支持的飞机：B777、B747-400、A330、A340"),
    RATING_4F(4, "机场最大支持的飞机：A380、B747-8")
    ;

    private final Integer id;
    private final String description;
}
