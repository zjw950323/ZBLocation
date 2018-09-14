package com.xunchijn.zblocation.util;

/**
 * Created by Administrator on 2018/5/7 0007.
 */

public class Result<T> {
    //状态码
    private int code;
    //错误信息 与 data互斥
    private String message;
    //正确的信息集合
    private T data;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
