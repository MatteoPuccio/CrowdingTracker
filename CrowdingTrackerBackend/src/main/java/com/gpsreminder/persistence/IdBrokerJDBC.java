package com.gpsreminder.persistence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class IdBrokerJDBC implements IdBroker {
	private Connection conn;

	public IdBrokerJDBC(Connection conn) {
		this.conn = conn;
	}

	public long getNextBookmarkId() throws SQLException {

		String query = "select nextval('gps_reminder.bookmark_id_seq') as id";

		Statement st = conn.createStatement();

		ResultSet rs = st.executeQuery(query);

		rs.next();

		return rs.getLong("id");
	}

	public long getNextUserId() throws SQLException {
		String query = "select nextval('gps_reminder.user_id_seq') as id";

		Statement st = conn.createStatement();

		ResultSet rs = st.executeQuery(query);

		rs.next();

		return rs.getLong("id");
	}

	public long getNextPasswordTokenId() throws SQLException {
		String query = "select nextval('gps_reminder.password_token_id_seq') as id";

		Statement st = conn.createStatement();

		ResultSet rs = st.executeQuery(query);

		rs.next();

		return rs.getLong("id");
	}

	@Override
	public long getNextVenueInfoId() throws SQLException {
		String query = "select nextval('gps_reminder.venue_information_id_seq') as id";

		Statement st = conn.createStatement();

		ResultSet rs = st.executeQuery(query);

		rs.next();

		return rs.getLong("id");
	}

}
