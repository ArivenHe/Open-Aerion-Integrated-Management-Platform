package cn.ariven.openaimpbackend.service;

public interface CommonService {
    String getFsdAccessTokenBySystemAdmin();
    String getFsdAccessTokenByAuth(Integer cid,String password);
}
