package com.dopamines.backend.security;

public class JwtConstants {

    // Expiration Time
    public static final long MINUTE = 1000 * 60;
    public static final long HOUR = 60 * MINUTE;
    public static final long DAY = 24 * HOUR;
    public static final long MONTH = 30 * DAY;

    public static final long AT_EXP_TIME =  900000 * MINUTE;
    public static final long RT_EXP_TIME =  900000 * MINUTE;

    // Secret
    public static final String JWT_SECRET = "e9502266904ad5f16469bddbd9c105cfe02543a2eb9f0bd498b0597a511955473cda9249979274e78adb33474179f443f22f2c98f98c685656915a2fcead9b60";

    // Header
    public static final String AT_HEADER = "access_token";
    public static final String RT_HEADER = "refresh_token";
    public static final String TOKEN_HEADER_PREFIX = "Bearer ";
}
