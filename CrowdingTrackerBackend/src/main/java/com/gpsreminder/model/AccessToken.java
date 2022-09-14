package com.gpsreminder.model;

import java.sql.Date;
import java.util.Calendar;

import com.gpsreminder.utils.Common;

public class AccessToken {

	private String token;
	private User user;
	private Date expirationDate;

	public AccessToken() {}
	
	public AccessToken(User user) {
		this.token = Common.generateAccessToken();
		this.user = user;

		Calendar date = Calendar.getInstance();
		date.add(Calendar.DAY_OF_MONTH, 20);
		this.expirationDate = new Date(date.getTimeInMillis());
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public boolean isExpired() {
		boolean expired = false;
		java.util.Date now = new java.util.Date();
		if(now.after(expirationDate)) {
			System.out.println(now.toString());
			expired = true;
		}
		return expired;
	}

	@Override
	public String toString() {
		return "AccessToken [token=" + token + ", user=" + user + ", expirationDate=" + expirationDate + "]";
	}

	
	
}
