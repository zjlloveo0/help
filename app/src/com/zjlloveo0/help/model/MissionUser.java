package com.zjlloveo0.help.model;

public class MissionUser extends Mission {

    private String createrName;
    private String createrImg;
    private String receiverName;
    private String receiverImg;

    public String getCreaterName() {
        return createrName;
    }

    public void setCreaterName(String createrName) {
        this.createrName = createrName;
    }

    public String getCreaterImg() {
        return createrImg;
    }

    public void setCreaterImg(String createrImg) {
        this.createrImg = createrImg;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverImg() {
        return receiverImg;
    }

    public void setReceiverImg(String receiverImg) {
        this.receiverImg = receiverImg;
    }

    @Override
    public String toString() {
        return "{\"createrName\":\"" + createrName + "\",\"createrImg\":\"" + createrImg
                + "\",\"receiverName\":\"" + receiverName + "\",\"receiverImg\":\"" + receiverImg
                + "\",\"id\":" + getId() + ",\"createrId\":"
                + getCreaterId() + ",\"receiverId\":"
                + getReceiverId() + ",\"createTime\":\""
                + getCreateTime() + "\",\"finishTime\":\""
                + getFinishTime() + "\",\"title\":\"" + getTitle()
                + "\",\"content\":\"" + getContent()
                + "\",\"exchangePoint\":" + getExchangePoint()
                + ",\"isEnable\":" + getIsEnable()
                + ",\"getUpdateTime()\":\"" + getUpdateTime() + "\"}";
    }

}
