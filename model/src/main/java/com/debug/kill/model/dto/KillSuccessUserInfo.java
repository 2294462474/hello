package com.debug.kill.model.dto;

import com.debug.kill.model.entity.ItemKillSuccess;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class KillSuccessUserInfo extends ItemKillSuccess implements Serializable {//dto在前端界面和后端之间传输数据

    private String userName;
    private String phone;
    private String email;
    private String itemName;

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getUserName() {
        return userName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getItemName() {
        return itemName;
    }
}
