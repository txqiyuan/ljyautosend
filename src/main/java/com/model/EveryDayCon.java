package com.model;

import java.util.Date;

public class EveryDayCon {

    private int id;

    private int type; //1早，2中，3晚

    private String content;

    private Date updatetime;

    private String remark;

    public EveryDayCon() {
    }

    public EveryDayCon(int id, int type, String content, Date updatetime, String remark) {
        this.id = id;
        this.type = type;
        this.content = content;
        this.updatetime = updatetime;
        this.remark = remark;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "EveryDayCon{" +
                "id=" + id +
                ", type=" + type +
                ", content='" + content + '\'' +
                ", updatetime=" + updatetime +
                ", remark='" + remark + '\'' +
                '}';
    }
}
