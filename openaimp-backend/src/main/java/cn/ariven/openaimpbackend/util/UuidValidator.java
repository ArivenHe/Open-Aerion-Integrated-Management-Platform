package cn.ariven.openaimpbackend.util;

import java.util.UUID;

public class UuidValidator {
    public static boolean isUuid(String str) {
        try {
            UUID.fromString(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
