package com.gpsreminder.persistence.dao;

import java.sql.SQLException;

import com.gpsreminder.model.PasswordToken;
import com.gpsreminder.model.User;

public interface PasswordTokenDao {
	
	public PasswordToken findById(long id) throws SQLException;
	
	public PasswordToken findByUser(User user) throws SQLException;
	
	public void saveOrUpdate(PasswordToken token) throws SQLException;
	
	public void delete(long id) throws SQLException;
}
