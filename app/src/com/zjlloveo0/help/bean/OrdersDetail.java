package com.zjlloveo0.help.bean;

import com.zjlloveo0.help.utils.SystemUtil;

public class OrdersDetail extends Orders {
    private String img;
    private String title;
    private String content;
    private UserSchool createUser;
    private UserSchool ofUser;

    public OrdersDetail() {
    }

    public UserSchool getCreateUser() {
        return createUser;
    }

    public void setCreateUser(UserSchool createUser) {
        this.createUser = createUser;
    }

    public UserSchool getOfUser() {
        return ofUser;
    }

    public void setOfUser(UserSchool ofUser) {
        this.ofUser = ofUser;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "{\"img\":\"" + img + "\",\"title\":\"" + title
                + "\",\"reMsg\":\"" + getReMsg()
                + "\",\"serverId\":" + getServerId()
                + ",\"message\":\"" + getMessage() + "\",\"id\":"
                + getId() + ",\"createId\":" + getCreateId()
                + ",\"uId\":" + getUId() + ",\"state\":"
                + getState() + ",\"exchangePoint\":"
                + getExchangePoint() + ",\"isEnable()\":"
                + getIsEnable() + ",\"updateTime\":\"" + SystemUtil.formatDate(getUpdateTime())
                + "\",\"createUser\":" + createUser.toString()
                + ",\"ofUser\":" + ofUser.toString() + "}";
    }
}
