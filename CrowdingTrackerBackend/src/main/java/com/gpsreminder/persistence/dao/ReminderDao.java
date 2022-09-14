package com.gpsreminder.persistence.dao;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import com.gpsreminder.model.Reminder;

public interface ReminderDao {

	public Reminder findByPrimaryKey(Long id) throws SQLException;
	
	public List<Reminder> findActivesGreaterThanTimestamp(Timestamp timestamp) throws SQLException;
	
	public void saveOrUpdate(Reminder reminder) throws SQLException;
	
	public void delete(Long id) throws SQLException;
}
