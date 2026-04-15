package cn.ariven.openaimpbackend.config;

import cn.ariven.openaimpbackend.service.RbacService;
import cn.dev33.satoken.stp.StpInterface;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SaTokenPermissionProvider implements StpInterface {
  private final RbacService rbacService;

  @Override
  public List<String> getPermissionList(Object loginId, String loginType) {
    return rbacService.getPermissionCodesByUserId(parseUserId(loginId));
  }

  @Override
  public List<String> getRoleList(Object loginId, String loginType) {
    return rbacService.getRoleCodesByUserId(parseUserId(loginId));
  }

  private Integer parseUserId(Object loginId) {
    return Integer.parseInt(String.valueOf(loginId));
  }
}
