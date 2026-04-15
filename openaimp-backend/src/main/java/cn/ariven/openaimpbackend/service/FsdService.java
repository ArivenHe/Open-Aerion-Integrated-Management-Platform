package cn.ariven.openaimpbackend.service;

import cn.ariven.openaimpbackend.dto.request.RequestFsdCreateUser;
import cn.ariven.openaimpbackend.dto.request.RequestFsdIssueToken;
import cn.ariven.openaimpbackend.dto.response.ResponseFsdIssueToken;
import cn.ariven.openaimpbackend.dto.response.ResponseFsdOnlineUsers;
import cn.ariven.openaimpbackend.dto.response.ResponseFsdTokenClaims;
import cn.ariven.openaimpbackend.dto.response.ResponseFsdUser;

public interface FsdService {
  ResponseFsdIssueToken issueToken(RequestFsdIssueToken requestFsdIssueToken);

  ResponseFsdIssueToken issueServiceToken();

  ResponseFsdIssueToken issueAccessToken();

  ResponseFsdTokenClaims parseToken(String token);

  ResponseFsdOnlineUsers getOnlineUsers();

  ResponseFsdUser createUser(RequestFsdCreateUser requestFsdCreateUser);

  void kickUser(String callsign);

  boolean isValidClientCallsign(String callsign);

  boolean isAllowedFacilityType(Integer networkRating, Integer facilityType);

  boolean isSupportedProtocolRevision(Integer protocolRevision);
}
