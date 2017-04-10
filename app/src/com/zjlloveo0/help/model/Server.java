package com.zjlloveo0.help.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 服务
 *
 * @author zjlloveo0
 */
public class Server implements Serializable {
    private static final long serialVersionUID = -534655350232993010L;
    private Integer id;
    private Integer createrId;
    private Date createTime;
    private String title;
    private String content;
    private String img;
    private Integer exchangePoint;
    private Integer isEnable;
    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCreaterId() {
        return createrId;
    }

    public void setCreaterId(Integer createrId) {
        this.createrId = createrId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
        return "{\"id\":\"" + id + "\",\"createrId\":\"" + createrId
                + "\",\"createTime\":\"" + createTime + "\",\"title\":\""
                + title + "\",\"content\":\"" + content + "\",\"img\":\"" + img
                + "\",\"exchangePoint\":\"" + exchangePoint
                + "\",\"isEnable\":\"" + isEnable + "\",\"updateTime\":\""
                + updateTime + "\"}";
    }
}
