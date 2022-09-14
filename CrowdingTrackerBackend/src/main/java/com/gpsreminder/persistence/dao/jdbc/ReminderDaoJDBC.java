package com.gpsreminder.persistence.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.gpsreminder.model.Reminder;
import com.gpsreminder.persistence.DatabaseManager;
import com.gpsreminder.persistence.dao.ReminderDao;

public class ReminderDaoJDBC implements ReminderDao {

	private Connection conn;

	public ReminderDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public Reminder findByPrimaryKey(Long id) throws SQLException {
		Reminder reminder = null;

		String query = "select * from gps_reminder.reminders where bookmark_id = ?";

		PreparedStatement st = conn.prepareStatement(query);

		st.setLong(1, id);

		ResultSet rs = st.executeQuery();

		if (rs.next()) {
			reminder = new Reminder();

			reminder.setBookmark(
					DatabaseManager.getInstance().getBookmarkDao().findByPrimaryKey(rs.getLong("bookmark_id")));
			reminder.setEnabled(rs.getBoolean("enabled"));
			reminder.setTimestamp(rs.getTimestamp("timestamp"));
			reminder.setTokenFCM(rs.getString("token_FCM"));
			reminder.setLanguage(rs.getString("language"));
		}

		rs.close();
		st.close();

		return reminder;
	}

	@Override
	public void saveOrUpdate(Reminder reminder) throws SQLException {
		String query = "select * from gps_reminder.reminders where bookmark_id = ?";

		PreparedStatement st = conn.prepareStatement(query);

		st.setLong(1, reminder.getBookmark().getId());

		ResultSet rs = st.executeQuery();

		if (rs.next()) {
			query = "update gps_reminder.reminders set " + "enabled = ?," + "timestamp = ?," + "token_FCM = ?,"
					+ "language = ?"+ " where bookmark_id = ?";

			PreparedStatement updateSt = conn.prepareStatement(query);

			updateSt.setBoolean(1, reminder.getEnabled());
			updateSt.setTimestamp(2, reminder.getTimestamp());
			updateSt.setString(3, reminder.getTokenFCM());
			updateSt.setString(4, reminder.getLanguage());
			updateSt.setLong(5, reminder.getBookmark().getId());

			updateSt.executeUpdate();
			updateSt.close();
		} else {
			query = "insert into gps_reminder.reminders values(?,?,?,?,?)";

			PreparedStatement insertSt = conn.prepareStatement(query);

			insertSt.setLong(1, reminder.getBookmark().getId());
			insertSt.setBoolean(2, reminder.getEnabled());
			insertSt.setTimestamp(3, reminder.getTimestamp());
			insertSt.setString(4, reminder.getTokenFCM());
			insertSt.setString(5, reminder.getLanguage());

			insertSt.executeUpdate();
			insertSt.close();
		}

		rs.close();
		st.close();
	}

	@Override
	public void delete(Long id) throws SQLException {
		String query = "delete from gps_reminder.reminders where bookmark_id = ?";

		PreparedStatement st = conn.prepareStatement(query);

		st.setLong(1, id);

		st.executeUpdate();
	}

	@Override
	public List<Reminder> findActivesGreaterThanTimestamp(Timestamp timestamp) throws SQLException {

		List<Reminder> reminders = new ArrayList<Reminder>();

		String query = "select * from gps_reminder.reminders where enabled = ? and timestamp < ?";

		PreparedStatement st = conn.prepareStatement(query);

		st.setBoolean(1, true);
		st.setTimestamp(2, timestamp);

		ResultSet rs = st.executeQuery();

		while (rs.next()) {
			Reminder reminder = new Reminder();

			reminder.setEnabled(true);
			reminder.setBookmark(
					DatabaseManager.getInstance().getBookmarkDao().findByPrimaryKey(rs.getLong("bookmark_id")));
			reminder.setTimestamp(rs.getTimestamp("timestamp"));
			reminder.setTokenFCM(rs.getString("token_FCM"));
			reminder.setLanguage(rs.getString("language"));
			
			reminders.add(reminder);
		}

		return reminders;
	}
}
