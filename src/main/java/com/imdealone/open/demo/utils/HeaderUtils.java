package com.imdealone.open.demo.utils;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public final class HeaderUtils {

    private HeaderUtils() {
    }

    public static String getHeaderValue(String headerName) {
        // 获取 ServletRequestAttributes
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return null;
        }
        // 获取 HttpServletRequest 对象
        HttpServletRequest request = requestAttributes.getRequest();
        // 获取指定 header 的值
        return request.getHeader(headerName);
    }

//    public static Enumeration<String> Headers() {
//        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        if (requestAttributes == null) {
//            return null;
//        }
//        // 获取 HttpServletRequest 对象
//        HttpServletRequest request = requestAttributes.getRequest();
//        return request.getHeaderNames();
//    }


}