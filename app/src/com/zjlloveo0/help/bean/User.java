package com.zjlloveo0.help.bean;

import java.util.Date;

import com.zjlloveo0.help.utils.SystemUtil;

/**
 * 用户
 * @author zjlloveo0
 *
 */
public class User {
    private Integer id;
	private String name;
	private String phone;
	private String password;
	private String img;
    private String schoolInfo;
    private String stuNum;
	private Integer point;
	private Integer collegeId;
	private Integer star;
	private Integer isEnable;
	private Date updateTime;

    public String getSchoolInfo() {
        return schoolInfo;
    }

    public void setSchoolInfo(String schoolInfo) {
        this.schoolInfo = schoolInfo;
    }

    public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public String getStuNum() {
		return stuNum;
	}
	public void setStuNum(String stuNum) {
		this.stuNum = stuNum;
	}
	public Integer getPoint() {
		return point;
	}
	public void setPoint(Integer point) {
		this.point = point;
	}
	public Integer getCollegeId() {
		return collegeId;
	}
	public void setCollegeId(Integer collegeId) {
		this.collegeId = collegeId;
	}
	public Integer getStar() {
		return star;
	}
	public void setStar(Integer star) {
		this.star = star;
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
		return "{\"id\":" + id + ",\"name\":\"" + name + "\",\"phone\":\""
                + phone + "\",\"password\":\"" + password + "\",\"schoolInfo\":\"" + schoolInfo + "\",\"img\":\""
                + img + "\",\"stuNum\":\"" + stuNum + "\",\"point\":" + point
				+ ",\"collegeId\":" + collegeId + ",\"star\":" + star
				+ ",\"isEnable\":" + isEnable + ",\"updateTime\":\""
                + SystemUtil.formatDate(updateTime) + "\"}";
    }

}