package mywild.wildguide.framework.security.jwt;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public final class JwtUtils {

    private JwtUtils() {
    }

    public static long getUserIdFromJwt(JwtAuthenticationToken jwtToken) {
        return (long) jwtToken.getTokenAttributes().get(TokenConstants.JWT_USER_ID);
    }

    public static String getUsernameFromJwt(JwtAuthenticationToken jwtToken) {
        return jwtToken.getTokenAttributes().get(TokenConstants.JWT_USER_NAME).toString();
    }

}
