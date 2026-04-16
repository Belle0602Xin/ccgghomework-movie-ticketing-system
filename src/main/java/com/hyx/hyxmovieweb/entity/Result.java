package com.hyx.hyxmovieweb.entity;

import lombok.Data;

@Data
public class Result {
    public int code;
    public String message;
    public Object data;

    public static Result success(Object data) {
        Result res = new Result();
        res.code = 0;
        res.message = "成功";
        res.data = data;

        return res;
    }

    public static Result ok(String message, Object data) {
        Result res = new Result();
        res.code = 200;
        res.message = message;
        res.data = data;

        return res;
    }

    public static Result ok(String message) {
        return ok(message, null);
    }

    public static Result error(String message, Object data) {
        Result res = new Result();
        res.code = 400;
        res.message = message;
        res.data = data;

        return res;
    }

    public static Result error(String message) {
        return error(message, null);
    }
}