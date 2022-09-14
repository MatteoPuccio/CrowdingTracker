package com.gpsreminder.persistence.dao;

import java.sql.SQLException;

import com.gpsreminder.model.AccessToken;
import com.gpsreminder.model.User;

public interface AccessTokenDao {
			
	public void saveOrUpdate(AccessToken token) throws SQLException;
	
	public void delete(Long id) throws SQLException;

	public AccessToken findByUser(User user) throws SQLException;

	public AccessToken findByToken(String token) throws SQLException;
	
}
