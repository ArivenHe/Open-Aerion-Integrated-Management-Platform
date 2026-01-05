package cn.ariven.openaimpbackend.dto;

import lombok.Data;

@Data
public class ChangeUserRoleDTO {
    private int userId;
    private String roleList;
}
