package com.debug.kill.api.response;

import com.debug.kill.api.enums.StatucCode;

public class BaseResponse<T> {
    private Integer code;
    private String msg;
    private T data;
    public BaseResponse(Integer code,String msg)
    {
        this.code=code;
        this.msg=msg;
    }
    public BaseResponse(StatucCode statucCode)
    {
        this.code=statucCode.getCode();
        this.msg=statucCode.getMsg();
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public BaseResponse(Integer code, String msg , T dada){
        this.code=code;
        this.msg=msg;
        this.data=dada;
    }

}
