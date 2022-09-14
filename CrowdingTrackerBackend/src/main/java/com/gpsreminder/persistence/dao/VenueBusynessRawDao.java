package com.gpsreminder.persistence.dao;

import java.sql.SQLException;
import java.util.List;

import com.gpsreminder.model.VenueBusynessRaw;

public interface VenueBusynessRawDao {
	
	public List<VenueBusynessRaw> findByVenueInfoId(String venueInfoId) throws SQLException;
	
	public void saveOrUpdate(VenueBusynessRaw venueBusynessRaw) throws SQLException;

	public void delete(String venueInfoId) throws SQLException;
}
