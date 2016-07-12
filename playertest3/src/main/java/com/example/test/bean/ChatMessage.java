package com.example.test.bean;

public class ChatMessage {
	/**
	 * type : 1
	 * id : -16
	 * username : visitor-16
	 * msg : Hello, world!
	 * datetime : 2016-06-24 10:48:09
	 */

	private int type;
	private int id;
	private String username;
	private String msg;
	private String datetime;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	@Override
	public String toString() {
		return "ChatMessage{" +
				"type=" + type +
				", id=" + id +
				", username='" + username + '\'' +
				", msg='" + msg + '\'' +
				", datetime='" + datetime + '\'' +
				'}';
	}
/*
	// 消息类型 1：上线消息
	public Integer type;        
	// 用户id
	public Integer id;
	// 用户名
	public String username;
	// 信息
	public String msg;
	// 消息时间
	public Date datetime;
	
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Date getDatetime() {
		return datetime;
	}
	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}
	*/
}
