package com.gpsreminder.persistence.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.gpsreminder.model.AccessToken;
import com.gpsreminder.model.User;
import com.gpsreminder.persistence.DatabaseManager;
import com.gpsreminder.persistence.dao.AccessTokenDao;

public class AccessTokenDaoJDBC implements AccessTokenDao{

	private Connection conn;
	
	public AccessTokenDaoJDBC(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public AccessToken findByUser(User user) throws SQLException {
		
		AccessToken token = null;
		
		String query = "select * from gps_reminder.access_tokens where user_id = ?";
		
		PreparedStatement st = conn.prepareStatement(query);
				
		st.setLong(1, user.getId());
		
		ResultSet rs = st.executeQuery();
		
		while(rs.next()) {
			token = new AccessToken();
			
			token.setExpirationDate(rs.getDate("expiration_date"));
			token.setToken(rs.getString("token"));
			token.setUser(user);
		}
		
		st.close();
		return token;
	}

	@Override
	public void saveOrUpdate(AccessToken token) throws SQLException {
		String query = "select * from gps_reminder.access_tokens where user_id = ?";

		PreparedStatement st = conn.prepareStatement(query);
		st.setLong(1, token.getUser().getId());

		ResultSet rs = st.executeQuery();

		if (rs.next()) {
			query = "update gps_reminder.access_tokens set " + "token = ?," + "expiration_date = ?"
					+ " where user_id = ?";

			PreparedStatement updateSt = conn.prepareStatement(query);

			System.out.println(token.toString());
			
			updateSt.setString(1, token.getToken());
			updateSt.setDate(2, token.getExpirationDate());
			updateSt.setLong(3, token.getUser().getId());

			updateSt.executeUpdate();
			updateSt.close();
		} else {
			query = "insert into gps_reminder.access_tokens values(?,?,?)";

			PreparedStatement insertSt = conn.prepareStatement(query);

			insertSt.setString(1, token.getToken());
			insertSt.setLong(2, token.getUser().getId());
			insertSt.setDate(3, token.getExpirationDate());

			insertSt.executeUpdate();
			insertSt.close();
		}

		st.close();
		
	}

	@Override
	public void delete(Long id) throws SQLException {
		String query = "delete from gps_reminder.access_tokens where user_id = ?";

		PreparedStatement st = conn.prepareStatement(query);

		st.setLong(1, id);

		st.executeUpdate();

		st.close();
		
	}

	@Override
	public AccessToken findByToken(String tokenString) throws SQLException {
		AccessToken token = null;
		
		String query = "select * from gps_reminder.access_tokens where token = ?";
		
		PreparedStatement st = conn.prepareStatement(query);
				
		st.setString(1, tokenString);
		
		ResultSet rs = st.executeQuery();
		
		if(rs.next()) {
			token = new AccessToken();
			
			token.setExpirationDate(rs.getDate("expiration_date"));
			token.setToken(tokenString);
			token.setUser(DatabaseManager.getInstance().getUserDao().findByPrimaryKey(rs.getLong("user_id")));
		}
		
		st.close();
		return token;
	}
		
}
