package com.gpsreminder.persistence.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.gpsreminder.model.User;
import com.gpsreminder.persistence.dao.UserDao;

public class UserDaoJDBC implements UserDao{

	private Connection conn;

	public UserDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public List<User> findAll() throws SQLException {
		List<User> users = new ArrayList<>();

		String query = "select * from gps_reminder.users order by id";

		Statement st = conn.createStatement();

		ResultSet rs = st.executeQuery(query);

		while (rs.next()) {

			User user = new User();

			user.setId(rs.getLong("id"));
			user.setEmail(rs.getString("email"));
			user.setHashedPassword(rs.getString("hashed_pw"));

			users.add(user);
		}

		return users;

	}

	@Override
	public User findByPrimaryKey(Long id) throws SQLException {
		User user = null;

		String query = "select * from gps_reminder.users where id = ?";

		PreparedStatement st = conn.prepareStatement(query);

		st.setLong(1, id);

		ResultSet rs = st.executeQuery();

		if (rs.next()) {
			user = new User();

			user.setId(rs.getLong("id"));
			user.setEmail(rs.getString("email"));
			user.setHashedPassword(rs.getString("hashed_pw"));
			user.setEmailConfirmed(rs.getBoolean("email_confirmed"));
		}

		return user;
	}

	@Override
	public void saveOrUpdate(User user) throws SQLException {
		String query = "select * from gps_reminder.users where id = ?";

		PreparedStatement st = conn.prepareStatement(query);
		st.setLong(1, user.getId());

		ResultSet rs = st.executeQuery();

		if (rs.next()) {
			query = "update gps_reminder.users set " + "hashed_pw = ?, " + "email = ?,"
					+ "email_confirmed = ? " + "where id = ?";

			PreparedStatement updateSt = conn.prepareStatement(query);
			updateSt.setString(1, user.getHashedPassword());
			updateSt.setString(2, user.getEmail());
			updateSt.setBoolean(3, user.getEmailConfirmed());
			updateSt.setLong(4, user.getId());

			updateSt.executeUpdate();
			updateSt.close();
		} else {
			query = "insert into gps_reminder.users values(?,?,?,?)";

			PreparedStatement insertSt = conn.prepareStatement(query);

			insertSt.setLong(1, user.getId());
			insertSt.setString(2, user.getEmail());
			insertSt.setString(3, user.getHashedPassword());
			insertSt.setBoolean(4, user.getEmailConfirmed());
			
			insertSt.executeUpdate();
			insertSt.close();
		}
	}
	
	@Override
	public User findByEmail(String email) throws SQLException {
		User user = null;

		String query = "select * from gps_reminder.users where email = ?";

		PreparedStatement st = conn.prepareStatement(query);

		st.setString(1, email);

		ResultSet rs = st.executeQuery();

		if (rs.next()) {
			user = new User();

			user.setId(rs.getLong("id"));
			user.setEmail(rs.getString("email"));
			user.setHashedPassword(rs.getString("hashed_pw"));
			user.setEmailConfirmed(rs.getBoolean("email_confirmed"));
		}

		return user;
	}

	@Override
	public void delete(Long id) throws SQLException {

		String query = "delete from gps_reminder.users where id = ?";
		
		PreparedStatement st = conn.prepareStatement(query);
		
		st.setLong(1, id);
		
		st.executeUpdate();
	}

}
