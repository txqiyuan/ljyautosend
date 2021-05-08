package com.model;

import java.util.Date;

public class Alexhqline {

    private Integer id;

    private Double z;

    private Double q;

    private Double vj;

    private Integer state;

    private Integer mark;

    private Date tm;

    public Alexhqline() {
    }

    public Alexhqline(Integer id, Double z, Double q, Double vj, Integer state, Integer mark, Date tm) {
        this.id = id;
        this.z = z;
        this.q = q;
        this.vj = vj;
        this.state = state;
        this.mark = mark;
        this.tm = tm;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getZ() {
        return z;
    }

    public void setZ(Double z) {
        this.z = z;
    }

    public Double getQ() {
        return q;
    }

    public void setQ(Double q) {
        this.q = q;
    }

    public Double getVj() {
        return vj;
    }

    public void setVj(Double vj) {
        this.vj = vj;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getMark() {
        return mark;
    }

    public void setMark(Integer mark) {
        this.mark = mark;
    }

    public Date getTm() {
        return tm;
    }

    public void setTm(Date tm) {
        this.tm = tm;
    }

    @Override
    public String toString() {
        return "Alexhqline{" +
                "id=" + id +
                ", z=" + z +
                ", q=" + q +
                ", vj=" + vj +
                ", state=" + state +
                ", mark=" + mark +
                ", tm=" + tm +
                '}';
    }
}
