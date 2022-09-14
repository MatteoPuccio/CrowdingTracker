package com.gpsreminder.persistence;

import java.sql.SQLException;

public interface IdBroker {

	public long getNextBookmarkId() throws SQLException;
	
	public long getNextUserId() throws SQLException;
			
	public long getNextPasswordTokenId() throws SQLException;

	public long getNextVenueInfoId() throws SQLException;
}
