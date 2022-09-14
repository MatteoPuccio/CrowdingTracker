package com.gpsreminder.persistence.dao;

import java.sql.SQLException;
import java.util.List;

import com.gpsreminder.model.Bookmark;
import com.gpsreminder.model.User;

public interface BookmarkDao {
	
	public Bookmark findByPrimaryKey(Long id) throws SQLException;
	
	public List<Bookmark> findAllByUser(User user) throws SQLException;
				
	public void saveOrUpdate(Bookmark bookmark) throws SQLException;
	
	public void delete(Long id) throws SQLException;
}
