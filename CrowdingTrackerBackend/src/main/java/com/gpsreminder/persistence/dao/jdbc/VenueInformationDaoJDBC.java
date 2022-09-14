package com.gpsreminder.persistence.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.gpsreminder.model.VenueInformation;
import com.gpsreminder.persistence.DatabaseManager;
import com.gpsreminder.persistence.dao.VenueInformationDao;

public class VenueInformationDaoJDBC implements VenueInformationDao {

	private Connection conn;

	public VenueInformationDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public List<VenueInformation> findAll() throws SQLException {

		List<VenueInformation> venueInformationList = new ArrayList<VenueInformation>();

		String query = "select * from gps_reminder.venue_information";

		PreparedStatement st = conn.prepareStatement(query);

		ResultSet rs = st.executeQuery();

		while (rs.next()) {
			VenueInformation venueInformation = new VenueInformation();

			venueInformation.setId(rs.getString("id"));
			venueInformation.setName(rs.getString("name"));
			venueInformation.setAvgDwellTime(rs.getLong("avg_dwell_time"));
			venueInformation.setLatitude(rs.getDouble("latitude"));
			venueInformation.setLongitude(rs.getDouble("longitude"));
			venueInformation.setAddress(rs.getString("address"));

			venueInformationList.add(venueInformation);
		}
		rs.close();
		st.close();

		return venueInformationList;
	}

	@Override
	public VenueInformation findByPrimaryKey(String id) throws SQLException {
		VenueInformation venueInformation = null;

		String query = "select * from gps_reminder.venue_information where id = ?";

		PreparedStatement st = conn.prepareStatement(query);

		st.setString(1, id);

		ResultSet rs = st.executeQuery();

		while (rs.next()) {
			venueInformation = new VenueInformation();

			venueInformation.setId(id);
			venueInformation.setName(rs.getString("name"));
			venueInformation.setAvgDwellTime(rs.getLong("avg_dwell_time"));
			venueInformation.setLatitude(rs.getDouble("latitude"));
			venueInformation.setLongitude(rs.getDouble("longitude"));
			venueInformation.setAddress(rs.getString("address"));
		}
		rs.close();
		st.close();

		return venueInformation;
	}

	@Override
	public VenueInformation findByNameAndCoords(String name, Double latitude, Double longitude) throws SQLException {
		VenueInformation venueInformation = null;

		String query = "select * from gps_reminder.venue_information where name = ? and latitude = ? and longitude = ?";

		PreparedStatement st = conn.prepareStatement(query);

		st.setString(1, name);
		st.setDouble(2, latitude);
		st.setDouble(3, longitude);

		ResultSet rs = st.executeQuery();

		while (rs.next()) {
			venueInformation = new VenueInformation();

			venueInformation.setId(rs.getString("id"));
			venueInformation.setName(name);
			venueInformation.setAvgDwellTime(rs.getLong("avg_dwell_time"));
			venueInformation.setLatitude(latitude);
			venueInformation.setLongitude(longitude);
			venueInformation.setAddress(rs.getString("address"));
		}
		rs.close();
		st.close();

		return venueInformation;
	}

	@Override
	public void saveOrUpdate(VenueInformation venueInformation) throws SQLException {
		String query = "select * from gps_reminder.venue_information where id = ?";

		PreparedStatement st = conn.prepareStatement(query);

		st.setString(1, venueInformation.getId());

		ResultSet rs = st.executeQuery();

		if (rs.next()) {
			query = "update gps_reminder.venue_information set " + "name = ?," + "avg_dwell_time = ?," + "latitude = ?,"
					+ "longitude = ?, address = ?" + "where id = ?";

			PreparedStatement updateSt = conn.prepareStatement(query);
			updateSt.setString(1, venueInformation.getName());
			updateSt.setLong(2, venueInformation.getAvgDwellTime());
			updateSt.setDouble(3, venueInformation.getLatitude());
			updateSt.setDouble(4, venueInformation.getLongitude());
			updateSt.setString(5, venueInformation.getAddress());
			updateSt.setString(6, venueInformation.getId());

			updateSt.executeUpdate();
			updateSt.close();
		} else {
			query = "insert into gps_reminder.venue_information values(?,?,?,?,?,?)";

			PreparedStatement insertSt = conn.prepareStatement(query);

			insertSt.setString(1, venueInformation.getId());
			insertSt.setString(2, venueInformation.getName());
			insertSt.setLong(3, venueInformation.getAvgDwellTime());
			insertSt.setDouble(4, venueInformation.getLatitude());
			insertSt.setDouble(5, venueInformation.getLongitude());
			insertSt.setString(6, venueInformation.getAddress());
			
			insertSt.executeUpdate();
			insertSt.close();
		}

		st.close();

	}

	@Override
	public void delete(String id) throws SQLException {
		DatabaseManager.getInstance().getVenueBusynessRawDao().delete(id);

		String query = "delete from gps_reminder.venue_information where id = ?";

		PreparedStatement st = conn.prepareStatement(query);

		st.setString(1, id);

		st.executeUpdate();

	}

}
