package com.zjlloveo0.help.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.zjlloveo0.help.utils.SystemUtil;

import java.util.Date;

public class MissionUser extends Mission implements Parcelable {

    private String createrName;
    private String createrImg;
    private String receiverName;
    private String receiverImg;
    private Integer createrPoint;
    private Integer receiverPoint;

    public Integer getCreaterPoint() {
        return createrPoint;
    }

    public void setCreaterPoint(Integer createrPoint) {
        this.createrPoint = createrPoint;
    }

    public Integer getReceiverPoint() {
        return receiverPoint;
    }

    public void setReceiverPoint(Integer receiverPoint) {
        this.receiverPoint = receiverPoint;
    }

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
                + "\",\"id\":" + getId() + ",\"state\":" + getState() + ",\"createrId\":"
                + getCreaterId() + ",\"receiverId\":"
                + getReceiverId() + ",\"createTime\":\""
                + SystemUtil.formatDate(getCreateTime()) + "\",\"finishTime\":\""
                + SystemUtil.formatDate(getFinishTime()) + "\",\"title\":\"" + getTitle()
                + "\",\"content\":\"" + getContent()
                + "\",\"exchangePoint\":" + getExchangePoint()
                + ",\"createrPoint\":" + getCreaterPoint()
                + ",\"receiverPoint\":" + getReceiverPoint()
                + ",\"isEnable\":" + getIsEnable()
                + ",\"updateTime\":\"" + SystemUtil.formatDate(getUpdateTime()) + "\",\"img\":\"" + getImg() + "\"}";
    }

    public static final Parcelable.Creator<MissionUser> CREATOR = new Creator<MissionUser>() {

        @Override
        public MissionUser createFromParcel(Parcel source) {
            MissionUser missionUser = new MissionUser();
            missionUser.createrName = source.readString();
            missionUser.createrImg = source.readString();
            missionUser.receiverName = source.readString();
            missionUser.receiverImg = source.readString();
            String createrPoint = source.readString();
            String receiverPoint = source.readString();
            missionUser.createrPoint = Integer.valueOf((createrPoint == null || "null".equals(createrPoint) || "".equals(createrPoint)) ? "0" : createrPoint);
            missionUser.receiverPoint = Integer.valueOf((receiverPoint == null || "null".equals(receiverPoint) || "".equals(receiverPoint)) ? "0" : receiverPoint);
            String id = source.readString();
            missionUser.setId(Integer.valueOf((id == null || "null".equals(id) || "".equals(id)) ? "0" : id));
            String state = source.readString();
            missionUser.setState(state.equals("null") ? null : Integer.valueOf(state));
            String createrId = source.readString();
            String receiverId = source.readString();
            missionUser.setCreaterId(Integer.valueOf((createrId == null || "null".equals(createrId) || "".equals(createrId)) ? "0" : createrId));
            missionUser.setReceiverId(Integer.valueOf((receiverId == null || "null".equals(receiverId) || "".equals(receiverId)) ? "0" : receiverId));
            missionUser.setCreateTime(SystemUtil.convert(source.readString()));
            missionUser.setFinishTime(SystemUtil.convert(source.readString()));
            missionUser.setTitle(source.readString());
            missionUser.setContent(source.readString());
            missionUser.setImg(source.readString());
            String exchangePoint = source.readString();
            missionUser.setExchangePoint(Integer.valueOf((exchangePoint == null || "null".equals(exchangePoint) || "".equals(exchangePoint)) ? "0" : exchangePoint));
            String isEnable = source.readString();
            missionUser.setIsEnable(Integer.valueOf((isEnable == null || "null".equals(isEnable) || "".equals(isEnable)) ? "0" : isEnable));
            missionUser.setUpdateTime(SystemUtil.convert(source.readString()));
            return missionUser;
        }

        @Override
        public MissionUser[] newArray(int size) {
            return new MissionUser[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(createrName);
        dest.writeString(createrImg);
        dest.writeString(receiverName);
        dest.writeString(receiverImg);
        dest.writeString(createrPoint + "");
        dest.writeString(receiverPoint + "");
        dest.writeString(getId() + "");
        dest.writeString(getState() + "");
        dest.writeString(getCreaterId() + "");
        dest.writeString(getReceiverId() + "");
        dest.writeString(SystemUtil.formatDate(getCreateTime()));
        dest.writeString(SystemUtil.formatDate(getFinishTime()));
        dest.writeString(getTitle());
        dest.writeString(getContent());
        dest.writeString(getImg());
        dest.writeString(getExchangePoint() + "");
        dest.writeString(getIsEnable() + "");
        dest.writeString(SystemUtil.formatDate(getUpdateTime()));
    }
}
