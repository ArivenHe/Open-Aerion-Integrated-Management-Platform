package cn.ariven.openaimpbackend.service;

import cn.ariven.openaimpbackend.common.Result;
import cn.ariven.openaimpbackend.dto.request.activity.RequestActivityCreate;
import cn.ariven.openaimpbackend.dto.request.activity.RequestActivityId;
import cn.ariven.openaimpbackend.dto.request.activity.RequestActivityUpdate;
import cn.ariven.openaimpbackend.dto.response.activity.ResponseActivity;

import java.util.List;

public interface ActivityService {
    Result<ResponseActivity> create(RequestActivityCreate request);
    Result<ResponseActivity> update(RequestActivityUpdate request);
    Result<Void> delete(RequestActivityId request);
    Result<ResponseActivity> detail(RequestActivityId request);
    Result<List<ResponseActivity>> list();
    Result<ResponseActivity> signupPilot(RequestActivityId request);
    Result<ResponseActivity> signupAtc(RequestActivityId request);
}
