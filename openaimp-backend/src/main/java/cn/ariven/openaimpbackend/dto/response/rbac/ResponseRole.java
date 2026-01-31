package cn.ariven.openaimpbackend.dto.response.rbac;

import lombok.Data;
import java.util.List;

@Data
public class ResponseRole {
    private Long id;
    private String name;
    private String description;
    private List<String> permissions;
}
