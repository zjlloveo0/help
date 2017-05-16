package com.zjlloveo0.help.bean;

import com.zjlloveo0.help.utils.SystemUtil;

public class OrdersList extends Orders {
    private String img;
    private String title;

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
                + getExchangePoint() + ",\"isEnable\":"
                + getIsEnable() + ",\"updateTime\":\"" + SystemUtil.formatDate(getUpdateTime())
                + "\"}";
    }

}
