package cn.ariven.openaimpbackend.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FlightDirection {
    EAST(1, "推荐高度层：FL291/FL311/FL331/FL351/FL371/FL391"),
    WEST(2, "推荐高度层：FL301/FL321/FL341/FL361/FL381/FL401");

    private final Integer id;
    private final String description;
}
