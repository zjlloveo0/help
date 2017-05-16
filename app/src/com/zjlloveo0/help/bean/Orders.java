package com.zjlloveo0.help.bean;

import com.zjlloveo0.help.utils.SystemUtil;

import java.util.Date;

public class Orders {
    private Integer id;
    private Integer serverId;
    private Integer createId;
    private Integer uId;
    private String message;
    private String reMsg;
    private Integer state;
    private Integer exchangePoint;
    private Integer isEnable;
    private Date updateTime;

    public String getReMsg() {
        return reMsg;
    }

    public void setReMsg(String reMsg) {
        this.reMsg = reMsg;
    }

    public Integer getServerId() {
        return serverId;
    }

    public void setServerId(Integer serverId) {
        this.serverId = serverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCreateId() {
        return createId;
    }

    public void setCreateId(Integer createId) {
        this.createId = createId;
    }

    public Integer getUId() {
        return uId;
    }

    public void setUId(Integer uId) {
        this.uId = uId;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getExchangePoint() {
        return exchangePoint;
    }

    public void setExchangePoint(Integer exchangePoint) {
        this.exchangePoint = exchangePoint;
    }

    public Integer getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(Integer isEnable) {
        this.isEnable = isEnable;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "{\"id\":" + id + ",\"serverId\":" + serverId + ",\"createId\":"
                + createId + ",\"message\":\"" + message + "\",\"reMsg\":\""
                + reMsg + "\",\"uId\":" + uId + ",\"state\":" + state
                + ",\"exchangePoint\":" + exchangePoint + ",\"isEnable\":"
                + isEnable + ",\"updateTime\":\"" + SystemUtil.formatDate(updateTime) + "\"}";
    }
}