package com.gpsreminder.persistence.dao;

import java.sql.SQLException;
import java.util.List;

import com.gpsreminder.model.VenueInformation;

public interface VenueInformationDao {

	List<VenueInformation> findAll() throws SQLException;
	
	VenueInformation findByPrimaryKey(String id) throws SQLException;
	
	VenueInformation findByNameAndCoords(String name, Double latitude, Double longitude) throws SQLException;
	
	void saveOrUpdate(VenueInformation venueInformation) throws SQLException;
	
	void delete(String id) throws SQLException;
}
