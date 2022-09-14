package com.gpsreminder.persistence.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.gpsreminder.model.Bookmark;
import com.gpsreminder.model.User;
import com.gpsreminder.persistence.DatabaseManager;
import com.gpsreminder.persistence.dao.BookmarkDao;

public class BookmarkDaoJDBC implements BookmarkDao {

	private Connection conn;

	public BookmarkDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public Bookmark findByPrimaryKey(Long id) throws SQLException {
		Bookmark bookmark = null;

		String query = "select * from gps_reminder.bookmarks where id = ?";

		PreparedStatement st = conn.prepareStatement(query);

		st.setLong(1, id);

		ResultSet rs = st.executeQuery();

		if (rs.next()) {
			bookmark = new Bookmark();

			bookmark.setId(rs.getLong("id"));
			bookmark.setName(rs.getString("name"));
			bookmark.setUser(DatabaseManager.getInstance().getUserDao().findByPrimaryKey(rs.getLong("user_id")));
			bookmark.setInfo(
					DatabaseManager.getInstance().getVenueInformationDao().findByPrimaryKey(rs.getString("info_id")));
		}

		st.close();

		return bookmark;

	}

	@Override
	public void saveOrUpdate(Bookmark bookmark) throws SQLException {
		String query = "select * from gps_reminder.bookmarks where id = ?";

		PreparedStatement st = conn.prepareStatement(query);

		st.setLong(1, bookmark.getId());

		ResultSet rs = st.executeQuery();

		if (rs.next()) {
			query = "update gps_reminder.bookmarks set " + "user_id = ?," + "name = ?," + "info_id = ?"
					+ "where id = ?";

			PreparedStatement updateSt = conn.prepareStatement(query);
			updateSt.setLong(1, bookmark.getUser().getId());
			updateSt.setString(2, bookmark.getName());
			updateSt.setString(3, bookmark.getInfo().getId());
			updateSt.setLong(4, bookmark.getId());

			updateSt.executeUpdate();
			updateSt.close();
		} else {
			query = "insert into gps_reminder.bookmarks values(?,?,?,?)";

			PreparedStatement insertSt = conn.prepareStatement(query);

			insertSt.setLong(1, bookmark.getId());
			insertSt.setLong(2, bookmark.getUser().getId());
			insertSt.setString(3, bookmark.getName());
			insertSt.setString(4, bookmark.getInfo().getId());

			insertSt.executeUpdate();
			insertSt.close();
		}

		st.close();

	}

	@Override
	public void delete(Long id) throws SQLException {
		DatabaseManager.getInstance().getReminderDao().delete(id);
		
		String query = "delete from gps_reminder.bookmarks where id = ?";

		PreparedStatement st = conn.prepareStatement(query);

		st.setLong(1, id);

		st.executeUpdate();

	}

	@Override
	public List<Bookmark> findAllByUser(User user) throws SQLException {
		List<Bookmark> bookmarks = new ArrayList<>();

		String query = "select * from gps_reminder.bookmarks where user_id = ?";

		PreparedStatement st = conn.prepareStatement(query);

		st.setLong(1, user.getId());

		ResultSet rs = st.executeQuery();

		while (rs.next()) {

			Bookmark bookmark = new Bookmark();

			bookmark.setId(rs.getLong("id"));
			bookmark.setName(rs.getString("name"));
			bookmark.setUser(user);
			bookmark.setInfo(DatabaseManager.getInstance().getVenueInformationDao().findByPrimaryKey(rs.getString("info_id")));

			bookmarks.add(bookmark);
		}

		st.close();
		return bookmarks;

	}

}
