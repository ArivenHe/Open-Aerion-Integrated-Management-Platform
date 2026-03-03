package cn.ariven.openaimpbackend.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AtcGroup {
    STUDENT(1, "学员"),
    OFFICIAL(2, "放单"),
    INSTRUCTOR(3, "教员"),
    ADMINISTRATOR(4, "管理员");
    private final Integer id;
    private final String Group;
}
