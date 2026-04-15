package cn.ariven.openaimpbackend.dto.request;

import lombok.Data;

@Data
public class RequestCreatePermission {
  private String code;
  private String name;
  private String description;
}
