package com.mmall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

//notnull代表在序列化给前端的时候忽略空的字段，不在json中显示了
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse<T> {
    private int status;
    private String msg;
    private T data;

    private ServerResponse(int status) {
        this.status = status;
    }

    private ServerResponse(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    private ServerResponse(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    private ServerResponse(int status, T data) {
        this.status = status;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    //JsonIgnore代表该字段忽略，不被序列化传给前端
    @JsonIgnore
    public boolean isSuccess() {
        return this.status == ResponseCode.SUCCESS.getStatus();
    }

    public static <T> ServerResponse<T> success() {
        return new ServerResponse<>(ResponseCode.SUCCESS.getStatus());
    }

    public static <T> ServerResponse<T> successByMsg(String msg) {
        return new ServerResponse<>(ResponseCode.SUCCESS.getStatus(), msg);
    }

    public static <T> ServerResponse<T> successByData(T data) {
        return new ServerResponse<>(ResponseCode.SUCCESS.getStatus(), data);
    }

    public static <T> ServerResponse<T> success(String msg, T data) {
        return new ServerResponse<>(ResponseCode.SUCCESS.getStatus(), msg, data);
    }

    public static <T> ServerResponse<T> fail() {
        return new ServerResponse<>(ResponseCode.ERROR.getStatus(), ResponseCode.ERROR.getMsg());
    }

    public static <T> ServerResponse<T> fail(String msg) {
        return new ServerResponse<>(ResponseCode.ERROR.getStatus(), msg);
    }

    public static <T> ServerResponse<T> fail(int status, String msg) {
        return new ServerResponse<>(status, msg);
    }
}