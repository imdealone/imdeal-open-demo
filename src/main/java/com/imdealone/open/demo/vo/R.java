package com.imdealone.open.demo.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 611 on 2025/5/8.
 */
@Data
public class R<T> implements Serializable {
    private Integer code;
    private T data;
    private String message;


    public R(Integer code, T data, String message, Object... args) {
        this.code = code;
        this.data = data;
        this.message = args != null && args.length != 0 ? String.format(message, args) : message;
    }

    public boolean isSuccess() {
        return this.code >= 1;
    }


    public static <T> R<T> success(T data) {
        return new R<T>(1, data, "Success.", new Object[0]);
    }

    public static <T> R<T> failure(int code, String message, Object... args) {
        return new R<T>(code, null, message, args);
    }
}
