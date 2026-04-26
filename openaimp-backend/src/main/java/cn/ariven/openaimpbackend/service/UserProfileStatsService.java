package cn.ariven.openaimpbackend.service;

import cn.ariven.openaimpbackend.pojo.User;

public interface UserProfileStatsService {
  User ensureProfile(Integer userCid);

  void refreshStats(Integer userCid);
}
