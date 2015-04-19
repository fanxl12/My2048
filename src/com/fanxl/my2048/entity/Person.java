package com.fanxl.my2048.entity;

import cn.bmob.v3.BmobObject;

/**
 * 用户信息存储实体类
 */
public class Person extends BmobObject{
	
	private static final long serialVersionUID = 1L;
	
	private String nickname;
	private String gender;
	private String headImage;
    private int score;
	
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getHeadImage() {
		return headImage;
	}
	public void setHeadImage(String headImage) {
		this.headImage = headImage;
	}

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
