package com.gpsreminder.persistence.dao;

import java.sql.SQLException;

import com.gpsreminder.model.RegistrationToken;
import com.gpsreminder.model.User;

public interface RegistrationTokenDao {

	public RegistrationToken getUserRegistrationToken(User user) throws SQLException;
	
	public void saveOrUpdate(RegistrationToken token) throws SQLException;
	
	public void delete(User user) throws SQLException;
}
