package cn.ariven.openaimpbackend.service;

import cn.ariven.openaimpbackend.dto.Result;
import cn.ariven.openaimpbackend.dto.request.RequestAuthLoginEmail;
import cn.ariven.openaimpbackend.dto.request.RequestAuthRegisterEmail;
import cn.ariven.openaimpbackend.dto.response.ResponseAuthLoginEmail;
import cn.ariven.openaimpbackend.dto.response.ResponseAuthRegisterEmail;

public interface AuthService {
  Result<ResponseAuthRegisterEmail> registerByEmail(
      RequestAuthRegisterEmail requestAuthRegisterEmail);

  Result<ResponseAuthLoginEmail> loginEmail(RequestAuthLoginEmail requestAuthLoginEmail);
}
