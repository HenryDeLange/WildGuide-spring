package mywild.wildguide.framework.security.jwt;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public final class JwtUtils {

    private JwtUtils() {
    }

    public static long getUserIdFromJwt(JwtAuthenticationToken jwtToken) {
        if (jwtToken == null) {
            return 0;
        }
        return (long) jwtToken.getTokenAttributes().get(TokenConstants.JWT_USER_ID);
    }

    public static String getUsernameFromJwt(JwtAuthenticationToken jwtToken) {
        if (jwtToken == null) {
            return "anonymous";
        }
        return jwtToken.getTokenAttributes().get(TokenConstants.JWT_USER_NAME).toString();
    }

}
