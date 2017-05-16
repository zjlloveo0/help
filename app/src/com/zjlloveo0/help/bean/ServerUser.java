package com.zjlloveo0.help.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.zjlloveo0.help.utils.SystemUtil;

import java.util.Date;

public class ServerUser extends Server implements Parcelable {
    private static final long serialVersionUID = -6679540039631131864L;
    private String createrName;
    private String createrImg;
    private Integer createrPoint;

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

    public Integer getCreaterPoint() {
        return createrPoint;
    }

    public void setCreaterPoint(Integer createrPoint) {
        this.createrPoint = createrPoint;
    }

    @Override
    public String toString() {
        return "{\"createrName\":\"" + createrName + "\",\"createrImg\":\"" + createrImg
                + "\",\"createrPoint\":" + createrPoint + ",\"id\":" + getId() + ",\"createrId\":"
                + getCreaterId() + ",\"createTime\":\""
                + SystemUtil.formatDate(getCreateTime()) + "\",\"title\":\"" + getTitle()
                + "\",\"content\":\"" + getContent()
                + "\",\"exchangePoint\":" + getExchangePoint()
                + ",\"isEnable\":" + getIsEnable()
                + ",\"updateTime\":\"" + SystemUtil.formatDate(getUpdateTime()) + "\",\"img\":\"" + getImg() + "\"}";
    }

    public static final Parcelable.Creator<ServerUser> CREATOR = new Creator<ServerUser>() {

        @Override
        public ServerUser createFromParcel(Parcel source) {
            ServerUser serverUser = new ServerUser();
            serverUser.createrName = source.readString();
            serverUser.createrImg = source.readString();
            String createrPoint = source.readString();
            serverUser.createrPoint = Integer.valueOf((createrPoint == null || "null".equals(createrPoint) || "".equals(createrPoint)) ? "0" : createrPoint);
            String id = source.readString();
            serverUser.setId(Integer.valueOf((id == null || "null".equals(id) || "".equals(id)) ? "0" : id));
            String createrId = source.readString();
            serverUser.setCreaterId(Integer.valueOf((createrId == null || "null".equals(createrId) || "".equals(createrId)) ? "0" : createrId));
            serverUser.setCreateTime(SystemUtil.convert(source.readString()));
            serverUser.setTitle(source.readString());
            serverUser.setContent(source.readString());
            serverUser.setImg(source.readString());
            String exchangePoint = source.readString();
            serverUser.setExchangePoint(Integer.valueOf((exchangePoint == null || "null".equals(exchangePoint) || "".equals(exchangePoint)) ? "0" : exchangePoint));
            String isEnable = source.readString();
            serverUser.setIsEnable(Integer.valueOf((isEnable == null || "null".equals(isEnable) || "".equals(isEnable)) ? "0" : isEnable));
            serverUser.setUpdateTime(SystemUtil.convert(source.readString()));
            return serverUser;
        }

        @Override
        public ServerUser[] newArray(int size) {
            return new ServerUser[size];
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
        dest.writeString(createrPoint + "");
        dest.writeString(getId() + "");
        dest.writeString(getCreaterId() + "");
        dest.writeString(SystemUtil.formatDate(getCreateTime()));
        dest.writeString(getTitle());
        dest.writeString(getContent());
        dest.writeString(getImg());
        dest.writeString(getExchangePoint() + "");
        dest.writeString(getIsEnable() + "");
        dest.writeString(SystemUtil.formatDate(getUpdateTime()));
    }
}
