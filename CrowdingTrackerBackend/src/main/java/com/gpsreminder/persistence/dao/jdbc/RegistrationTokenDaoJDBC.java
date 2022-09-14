package com.gpsreminder.persistence.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.gpsreminder.model.RegistrationToken;
import com.gpsreminder.model.User;
import com.gpsreminder.persistence.dao.RegistrationTokenDao;

public class RegistrationTokenDaoJDBC implements RegistrationTokenDao {

	private Connection conn;

	public RegistrationTokenDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public RegistrationToken getUserRegistrationToken(User user) throws SQLException {

		RegistrationToken token = null;

		String query = "select * from gps_reminder.registration_tokens where user_id = ?";

		PreparedStatement st = conn.prepareStatement(query);
		st.setLong(1, user.getId());

		ResultSet rs = st.executeQuery();

		if (rs.next()) {
			token = new RegistrationToken();

			token.setToken(rs.getString("token"));
			token.setExpirationDate(rs.getTimestamp("expiration_date"));
			token.setUser(user);
		}

		rs.close();
		st.close();

		return token;
	}

	@Override
	public void saveOrUpdate(RegistrationToken token) throws SQLException {
		String query = "select * from gps_reminder.registration_tokens where user_id = ?";

		PreparedStatement st = conn.prepareStatement(query);
		st.setLong(1, token.getUser().getId());

		ResultSet rs = st.executeQuery();

		if (rs.next()) {
			query = "update gps_reminder.registration_tokens set " + "token = ?," + "expiration_date = ? "
					+ "where user_id = ?";

			PreparedStatement updateSt = conn.prepareStatement(query);
			updateSt.setString(1, token.getToken());
			updateSt.setTimestamp(2, token.getExpirationDate());
			updateSt.setLong(3, token.getUser().getId());

			updateSt.executeUpdate();
			updateSt.close();
		} else {
			query = "insert into gps_reminder.registration_tokens values(?,?,?)";

			PreparedStatement insertSt = conn.prepareStatement(query);

			insertSt.setLong(1, token.getUser().getId());
			insertSt.setString(2, token.getToken());
			insertSt.setTimestamp(3, token.getExpirationDate());

			insertSt.executeUpdate();
			insertSt.close();
		}

	}

	@Override
	public void delete(User user) throws SQLException {
		String query = "delete from gps_reminder.registration_tokens where user_id = ?";

		PreparedStatement st = conn.prepareStatement(query);
		st.setLong(1, user.getId());

		st.executeUpdate();
	}
}
