package com.imdealone.open.demo.utils;

import org.apache.commons.lang3.StringUtils;

public final class SecurityContextUtil {

//    private static final ThreadLocal<IdentityResp> context = new ThreadLocal<>();
    private static final String TOKEN_PREFIX = "Bearer ";

    private SecurityContextUtil() {
    }


    public static String getToken() {
        String token = HeaderUtils.getHeaderValue("Authorization");
        if (StringUtils.isBlank(token)) {
            throw new UnauthorizedException();
        }
        if (token.startsWith(TOKEN_PREFIX)) {
            return StringUtils.substringAfter(token, TOKEN_PREFIX);
        }
        throw new UnauthorizedException();
    }

//    public static void set(IdentityResp identityResp) {
//        context.set(identityResp);
//    }
//
//    public static IdentityResp get() {
//        return context.get();
//    }
//
//    public static void clear() {
//        context.remove();
//    }

}
