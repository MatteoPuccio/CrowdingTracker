package com.gpsreminder.persistence.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.gpsreminder.model.PasswordToken;
import com.gpsreminder.model.User;
import com.gpsreminder.persistence.DatabaseManager;
import com.gpsreminder.persistence.dao.PasswordTokenDao;

public class PasswordTokenDaoJDBC implements PasswordTokenDao {

	private Connection conn;

	public PasswordTokenDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public PasswordToken findByUser(User user) throws SQLException {
		PasswordToken token = null;

		String query = "select * from gps_reminder.password_tokens where user_id = ?";

		PreparedStatement st = conn.prepareStatement(query);

		st.setLong(1, user.getId());

		ResultSet rs = st.executeQuery();

		if (rs.next()) {
			token = new PasswordToken();

			token.setId(rs.getLong("id"));
			token.setReleaseTime(rs.getTimestamp("release_time"));
			token.setToken(rs.getString("token"));
			token.setUser(user);
		}

		st.close();
		return token;
	}

	@Override
	public void saveOrUpdate(PasswordToken token) throws SQLException {
		String query = "select * from gps_reminder.password_tokens where id = ?";

		PreparedStatement st = conn.prepareStatement(query);
		st.setLong(1, token.getId());

		ResultSet rs = st.executeQuery();

		if (rs.next()) {
			query = "update gps_reminder.password_tokens set " + "user_id = ?," + "release_time = ?," + "token = ?,"
					+ "where id = ?";

			PreparedStatement updateSt = conn.prepareStatement(query);
			updateSt.setDouble(1, token.getUser().getId());
			updateSt.setTimestamp(2, token.getReleaseTime());
			updateSt.setString(3, token.getToken());
			updateSt.setLong(4, token.getId());

			updateSt.executeUpdate();
			updateSt.close();
		} else {
			query = "insert into gps_reminder.password_tokens values(?,?,?,?)";

			PreparedStatement insertSt = conn.prepareStatement(query);

			insertSt.setLong(1, token.getUser().getId());
			insertSt.setLong(2, token.getId());
			insertSt.setTimestamp(3, token.getReleaseTime());
			insertSt.setString(4, token.getToken());

			insertSt.executeUpdate();
			insertSt.close();
		}

		st.close();
	}

	@Override
	public void delete(long id) throws SQLException {
		String query = "delete from gps_reminder.password_tokens where id = ?";

		PreparedStatement st = conn.prepareStatement(query);

		st.setLong(1, id);

		st.executeUpdate();

		st.close();
	}

	@Override
	public PasswordToken findById(long id) throws SQLException {
		PasswordToken token = null;

		String query = "select * from gps_reminder.password_tokens where id = ?";

		PreparedStatement st = conn.prepareStatement(query);

		st.setLong(1, id);

		ResultSet rs = st.executeQuery();

		if (rs.next()) {
			token = new PasswordToken();

			token.setId(rs.getLong("id"));
			token.setReleaseTime(rs.getTimestamp("release_time"));
			token.setToken(rs.getString("token"));
			token.setUser(DatabaseManager.getInstance().getUserDao().findByPrimaryKey(rs.getLong("user_id")));
		}

		st.close();
		return token;
	}

}
