package cn.ariven.openaimpbackend.constant;

import java.util.Set;

public final class FsdConstants {
  private FsdConstants() {}

  public static final String JWT_ISSUER = "openfsd";

  public static final String TOKEN_TYPE_FSD = "fsd";
  public static final String TOKEN_TYPE_FSD_SERVICE = "fsd_service";
  public static final String TOKEN_TYPE_ACCESS = "access";

  public static final int NETWORK_RATING_INACTIVE = -1;
  public static final int NETWORK_RATING_SUSPENDED = 0;
  public static final int NETWORK_RATING_OBSERVER = 1;
  public static final int NETWORK_RATING_STUDENT_1 = 2;
  public static final int NETWORK_RATING_STUDENT_2 = 3;
  public static final int NETWORK_RATING_STUDENT_3 = 4;
  public static final int NETWORK_RATING_CONTROLLER_1 = 5;
  public static final int NETWORK_RATING_CONTROLLER_2 = 6;
  public static final int NETWORK_RATING_CONTROLLER_3 = 7;
  public static final int NETWORK_RATING_INSTRUCTOR_1 = 8;
  public static final int NETWORK_RATING_INSTRUCTOR_2 = 9;
  public static final int NETWORK_RATING_INSTRUCTOR_3 = 10;
  public static final int NETWORK_RATING_SUPERVISOR = 11;
  public static final int NETWORK_RATING_ADMINISTRATOR = 12;

  public static final int PROTOCOL_REVISION_MIN = 100;
  public static final int PROTOCOL_REVISION_MAX = 101;

  public static final Set<String> RESERVED_CALLSIGNS = Set.of("SERVER", "CLIENT", "FP");
}
