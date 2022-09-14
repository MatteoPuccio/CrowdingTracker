package com.gpsreminder.persistence.dao;

import java.sql.SQLException;
import java.util.List;

import com.gpsreminder.model.User;


public interface UserDao {
	public List<User> findAll() throws SQLException;
		
	public User findByPrimaryKey(Long id) throws SQLException;
		
	public void saveOrUpdate(User user) throws SQLException;
	
	public void delete(Long id) throws SQLException;

	public User findByEmail(String email) throws SQLException;
}
