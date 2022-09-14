package com.gpsreminder.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.gpsreminder.persistence.dao.AccessTokenDao;
import com.gpsreminder.persistence.dao.BookmarkDao;
import com.gpsreminder.persistence.dao.PasswordTokenDao;
import com.gpsreminder.persistence.dao.RegistrationTokenDao;
import com.gpsreminder.persistence.dao.ReminderDao;
import com.gpsreminder.persistence.dao.UserDao;
import com.gpsreminder.persistence.dao.VenueBusynessRawDao;
import com.gpsreminder.persistence.dao.VenueInformationDao;
import com.gpsreminder.persistence.dao.jdbc.AccessTokenDaoJDBC;
import com.gpsreminder.persistence.dao.jdbc.BookmarkDaoJDBC;
import com.gpsreminder.persistence.dao.jdbc.PasswordTokenDaoJDBC;
import com.gpsreminder.persistence.dao.jdbc.RegistrationTokenDaoJDBC;
import com.gpsreminder.persistence.dao.jdbc.ReminderDaoJDBC;
import com.gpsreminder.persistence.dao.jdbc.UserDaoJDBC;
import com.gpsreminder.persistence.dao.jdbc.VenueBusynessRawDaoJDBC;
import com.gpsreminder.persistence.dao.jdbc.VenueInformationDaoJDBC;

public class DatabaseManager {

	private static DatabaseManager instance = null;

	private Connection conn;

	private UserDao userDao = null;
	private BookmarkDao bookmarkDao = null;
	private PasswordTokenDao passwordTokenDao = null;
	private AccessTokenDao accessTokenDao = null;
	private VenueInformationDao venueInformationDao = null;
	private VenueBusynessRawDao venueBusynessRawDao = null;
	private ReminderDao reminderDao = null;
	private RegistrationTokenDao registrationTokenDao = null;

	private IdBroker idBroker = null;

	private DatabaseManager() {
		try {
			conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/gps_reminder_db", "postgres",
					"postgres");
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			System.err.println("Failed to connect to the database");
		}
	}

	public static DatabaseManager getInstance() {
		if (instance == null)
			instance = new DatabaseManager();
		return instance;
	}

	public UserDao getUserDao() {
		if (userDao == null)
			userDao = new UserDaoJDBC(conn);
		return userDao;
	}

	public PasswordTokenDao getPasswordTokenDao() {
		if (passwordTokenDao == null)
			passwordTokenDao = new PasswordTokenDaoJDBC(conn);
		return passwordTokenDao;
	}

	public BookmarkDao getBookmarkDao() {
		if (bookmarkDao == null)
			bookmarkDao = new BookmarkDaoJDBC(conn);
		return bookmarkDao;
	}

	public AccessTokenDao getAccessTokenDao() {
		if (accessTokenDao == null)
			accessTokenDao = new AccessTokenDaoJDBC(conn);
		return accessTokenDao;
	}

	public VenueInformationDao getVenueInformationDao() {
		if (venueInformationDao == null)
			venueInformationDao = new VenueInformationDaoJDBC(conn);
		return venueInformationDao;
	}

	public VenueBusynessRawDao getVenueBusynessRawDao() {
		if (venueBusynessRawDao == null)
			venueBusynessRawDao = new VenueBusynessRawDaoJDBC(conn);
		return venueBusynessRawDao;
	}

	public ReminderDao getReminderDao() {
		if (reminderDao == null)
			reminderDao = new ReminderDaoJDBC(conn);
		return reminderDao;
	}
	
	public RegistrationTokenDao getRegistrationTokenDao() {
		if(registrationTokenDao == null)
			registrationTokenDao = new RegistrationTokenDaoJDBC(conn);
		return registrationTokenDao;
	}

	public IdBroker getIdBroker() {
		if (idBroker == null)
			idBroker = new IdBrokerJDBC(conn);
		return idBroker;
	}

	public void commit() throws SQLException {
		conn.commit();
	}

}
