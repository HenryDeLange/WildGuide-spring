package mywild.wildguide.framework.security.jwt;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public final class JwtUtils {

    public static final long UNKNOWN_USER_ID = -1L;

    public static final String UNKNOWN_USER_NAME = "ANONYMOUS_USER";

    private JwtUtils() {
    }

    public static long getUserIdFromJwt(JwtAuthenticationToken jwtToken) {
        if (jwtToken == null) {
            return UNKNOWN_USER_ID;
        }
        return (long) jwtToken.getTokenAttributes().get(TokenConstants.JWT_USER_ID);
    }

    public static String getUsernameFromJwt(JwtAuthenticationToken jwtToken) {
        if (jwtToken == null) {
            return UNKNOWN_USER_NAME;
        }
        return jwtToken.getTokenAttributes().get(TokenConstants.JWT_USER_NAME).toString();
    }

}
