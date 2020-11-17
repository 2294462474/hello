package com.debug.kill.api.enums;

import org.omg.CORBA.INTERNAL;

public enum StatucCode {
    Success(0,"success"),
    Fail(-1,"失败"),
    InvalidParams(201,"非法参数"),
    userNotLogin(202,"未登录"),

    ;
    private Integer code;
    private String  msg;
    StatucCode(Integer code,String msg){
        this.code=code;
        this.msg=msg;
    }
    public Integer getCode(){
        return code;
    }
    public void setCode(Integer code){
        this.code=code;
    }
    public String getMsg(){return msg;}
    private void setMsg(){
        this.msg=msg;

    }
}
