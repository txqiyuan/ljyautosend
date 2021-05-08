package com.wechat;

import java.io.*;

/**
 * Created by Tiffany on 2019-7-18.
 */
public class AzureToken implements Serializable {

    //获取token
    private String token;
    //ticket
    private String ticket;
    //获取token时间
    private Long addTime;
    //token保存时长
    private int expiresIn;

    public AzureToken() {
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getAddTime() {
        return addTime;
    }

    public void setAddTime(Long addTime) {
        this.addTime = addTime;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    @Override
    public String toString() {
        return "AzureToken{" +
                "token='" + token + '\'' +
                ", ticket='" + ticket + '\'' +
                ", addTime=" + addTime +
                ", expiresIn=" + expiresIn +
                '}';
    }
}
