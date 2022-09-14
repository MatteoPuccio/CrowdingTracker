package com.gpsreminder.persistence.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.gpsreminder.model.VenueBusynessRaw;
import com.gpsreminder.persistence.dao.VenueBusynessRawDao;

public class VenueBusynessRawDaoJDBC implements VenueBusynessRawDao {

	private Connection conn;

	public VenueBusynessRawDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public List<VenueBusynessRaw> findByVenueInfoId(String venueInfoId) throws SQLException {
		List<VenueBusynessRaw> venueBusynessRawList = new ArrayList<VenueBusynessRaw>();

		String query = "select * from gps_reminder.venue_busyness_raw where info_id = ?";

		PreparedStatement st = conn.prepareStatement(query);

		st.setString(1, venueInfoId);

		ResultSet rs = st.executeQuery();

		while (rs.next()) {
			VenueBusynessRaw venueBusynessRaw = new VenueBusynessRaw();

			venueBusynessRaw.setInfoId(venueInfoId);
			venueBusynessRaw.setWeekday(rs.getInt("weekday"));

			Integer[] busynessRawArray = (Integer[]) rs.getArray("busyness").getArray();

			List<Integer> busynessRawAsList = new ArrayList<Integer>();

			for (Integer i : busynessRawArray) {
				busynessRawAsList.add(i);
			}

			venueBusynessRaw.setBusyness(busynessRawAsList);
			venueBusynessRawList.add(venueBusynessRaw);
		}
		rs.close();
		st.close();

		return venueBusynessRawList;
	}

	@Override
	public void saveOrUpdate(VenueBusynessRaw venueBusynessRaw) throws SQLException {
		String query = "select * from gps_reminder.venue_busyness_raw where info_id = ? and weekday = ?";

		PreparedStatement st = conn.prepareStatement(query);

		st.setString(1, venueBusynessRaw.getInfoId());
		st.setLong(2, venueBusynessRaw.getWeekday());

		ResultSet rs = st.executeQuery();

		Integer[] busynessAsArraySQL = new Integer[24];

		for (int i = 0; i < venueBusynessRaw.getBusyness().size(); ++i) {
			busynessAsArraySQL[i] = venueBusynessRaw.getBusyness().get(i);
		}

		if (rs.next()) {
			query = "update gps_reminder.venue_busyness_raw set " + "busyness = ?,"
					+ "where info_id = ? and weekday = ?";

			PreparedStatement updateSt = conn.prepareStatement(query);

			updateSt.setArray(1, conn.createArrayOf("int4", busynessAsArraySQL));
			updateSt.setString(2, venueBusynessRaw.getInfoId());
			updateSt.setInt(3, venueBusynessRaw.getWeekday());

			updateSt.executeUpdate();
			updateSt.close();
		} else {
			query = "insert into gps_reminder.venue_busyness_raw values(?,?,?)";

			PreparedStatement insertSt = conn.prepareStatement(query);

			insertSt.setString(1, venueBusynessRaw.getInfoId());
			insertSt.setInt(2, venueBusynessRaw.getWeekday());
			insertSt.setArray(3, conn.createArrayOf("int4", busynessAsArraySQL));

			insertSt.executeUpdate();
			insertSt.close();
		}

		st.close();

	}

	@Override
	public void delete(String venueInfoId) throws SQLException {
		String query = "delete from gps_reminder.venue_busyness_raw where info_id = ?";

		PreparedStatement st = conn.prepareStatement(query);

		st.setString(1, venueInfoId);
		
		st.executeUpdate();
	}

}
