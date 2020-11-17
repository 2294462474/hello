package com.debug.kill.server.dto;


import jodd.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MailDto implements Serializable {

    private String subject;

    private String content;
    private String[]  tos;
    /*
    public MailDto(String subject, String content, String[] tos) {
        this.subject=subject;
        this.content=content;
        this.tos=tos;
    }*/

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTos(String[] tos) {
        this.tos = tos;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public String[] getTos() {
        return tos;
    }



}
