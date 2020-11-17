package com.debug.kill.server.dto;


import lombok.Data;
import lombok.ToString;
import org.omg.CORBA.INTERNAL;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ToString
public class KillDto implements Serializable {
    @NotNull
    private Integer killId;
    private Integer userId;

    public Integer getKillId() {
        return killId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setKillId(Integer killId) {
        this.killId = killId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }






}
