package com.gpsreminder.controller;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gpsreminder.model.AccessToken;
import com.gpsreminder.model.Bookmark;
import com.gpsreminder.model.Reminder;
import com.gpsreminder.model.VenueInformation;
import com.gpsreminder.persistence.DatabaseManager;

@RestController
public class BookmarksController {

	@PostMapping("/getUserBookmarks")
	public String getUserBookmarks(@RequestBody ObjectNode json) {

		JSONObject response = new JSONObject();

		if (json.get("token") == null) {
			response.put("malformedRequest", true);
			return response.toString();
		}

		String token = json.get("token").asText();

		List<Bookmark> bookmarks = new ArrayList<Bookmark>();

		try {
			AccessToken accessToken = DatabaseManager.getInstance().getAccessTokenDao().findByToken(token);
			if (accessToken == null) {
				response.put("userNotFound", true);
			} else {
				bookmarks = DatabaseManager.getInstance().getBookmarkDao().findAllByUser(accessToken.getUser());
				JSONArray bookmarksJSONArray = new JSONArray();
				for (Bookmark bookmark : bookmarks) {
					JSONObject bookmarkJSON = bookmark.toJSONObject();
					bookmarksJSONArray.put(bookmarkJSON);
				}
				response.put("bookmarks", bookmarksJSONArray);
				System.out.println(bookmarksJSONArray.toString());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return response.toString();
	}

	@PostMapping("/deleteBookmark")
	public String deleteBookmark(@RequestBody ObjectNode json) {
		JSONObject response = new JSONObject();

		if (json.get("token") == null || json.get("bookmark") == null) {
			response.put("missingParams", true);
			return response.toString();
		}

		String token = json.get("token").asText();
		Long bookmarkId = json.get("bookmark").asLong();

		AccessToken accessToken = null;
		Bookmark bookmark = null;
		try {
			accessToken = DatabaseManager.getInstance().getAccessTokenDao().findByToken(token);
			bookmark = DatabaseManager.getInstance().getBookmarkDao().findByPrimaryKey(bookmarkId);

			if (accessToken == null) {
				response.put("userNotFound", true);
			} else if (bookmark == null) {
				response.put("bookmarkNotFound", true);
			} else if (!bookmark.getUser().equals(accessToken.getUser())) {
				response.put("unauthorizedUser", true);
			} else {
				DatabaseManager.getInstance().getBookmarkDao().delete(bookmark.getId());
				DatabaseManager.getInstance().commit();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return response.toString();
	}

	@PostMapping("/addBookmark")
	public String addBookmark(@RequestBody ObjectNode json) {
		JSONObject response = new JSONObject();

		if (json.get("bookmark") == null || json.get("token") == null) {
			response.put("malformedRequest", true);
			return response.toString();
		}

		JSONObject bookmarkJSON = new JSONObject(json.get("bookmark").asText());
		String token = json.get("token").asText();

		Bookmark bookmark = new Bookmark();

		try {
			AccessToken accessToken = DatabaseManager.getInstance().getAccessTokenDao().findByToken(token);
			if (accessToken == null) {
				response.put("userNotFound", true);
				return response.toString();
			}

			VenueInformation bookmarkInfo = new VenueInformation();

			bookmark.setId(DatabaseManager.getInstance().getIdBroker().getNextBookmarkId());
			bookmark.setName(bookmarkJSON.getString("name"));
			bookmark.setUser(accessToken.getUser());

			bookmarkInfo = BestTimeController.addVenue(bookmark.getName(), bookmarkJSON.getDouble("latitude"),
					bookmarkJSON.getDouble("longitude"), bookmarkJSON.getString("address"));
			bookmark.setInfo(bookmarkInfo);

			Reminder reminder = new Reminder(bookmark);

			DatabaseManager.getInstance().getBookmarkDao().saveOrUpdate(bookmark);

			DatabaseManager.getInstance().getReminderDao().saveOrUpdate(reminder);

			DatabaseManager.getInstance().commit();

			System.out.println(bookmark.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return response.toString();
	}

	@PostMapping("/getBookmarkReminder")
	public String getBookmarkReminder(@RequestBody ObjectNode json) {
		JSONObject response = new JSONObject();

		if (json.get("bookmark") == null || json.get("token") == null) {
			response.put("malformedRequest", true);
			return response.toString();
		}

		String token = json.get("token").asText();
		Long bookmarkId = json.get("bookmark").asLong();

		try {
			Bookmark bookmark = DatabaseManager.getInstance().getBookmarkDao().findByPrimaryKey(bookmarkId);
			AccessToken accessToken = DatabaseManager.getInstance().getAccessTokenDao().findByToken(token);
			if (accessToken == null || bookmark == null) {
				response.put("status", "error");
				response.put("userNotFound", true);
				return response.toString();
			} else if (!bookmark.getUser().equals(accessToken.getUser())) {
				response.put("status", "error");
				response.put("unauthorizedRequest", true);
				return response.toString();
			} else {
				response.put("status", "okay");

				Reminder reminder = DatabaseManager.getInstance().getReminderDao().findByPrimaryKey(bookmarkId);
				if (reminder == null)
					reminder = new Reminder();
				reminder.setBookmark(bookmark);
				JSONObject reminderJSON = reminder.toJSONObject();

				response.put("reminder", reminderJSON);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println(response.toString());
		return response.toString();
	}

	@PostMapping("/setBookmarkReminder")
	public String setBookmarkReminder(@RequestBody ObjectNode json) {
		JSONObject response = new JSONObject();

		if (json.get("bookmark") == null || json.get("token") == null || json.get("reminder") == null) {
			response.put("malformedRequest", true);
			return response.toString();
		}

		String token = json.get("token").asText();
		Long bookmarkId = json.get("bookmark").asLong();

		System.out.println("REMINDER " + json.get("reminder").asText());
		JSONObject reminderJSON = new JSONObject(json.get("reminder").asText());

		try {
			Bookmark bookmark = DatabaseManager.getInstance().getBookmarkDao().findByPrimaryKey(bookmarkId);
			AccessToken accessToken = DatabaseManager.getInstance().getAccessTokenDao().findByToken(token);
			Reminder reminder = null;

			if (accessToken == null || bookmark == null) {
				response.put("status", "error");
				response.put("userNotFound", true);
				return response.toString();
			} else if (!bookmark.getUser().equals(accessToken.getUser())) {
				response.put("status", "error");
				response.put("unauthorizedRequest", true);
				return response.toString();
			} else {
				response.put("status", "okay");

				String dateString = reminderJSON.getString("timestamp"); // format: yyyy-MM-dd'T'HH:mm:ss.SSSZ
				dateString = dateString.split("\\.")[0];
				
				SimpleDateFormat sdt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				sdt.setTimeZone(TimeZone.getTimeZone("UTC"));
				
				Timestamp date = new Timestamp(sdt.parse(dateString).getTime());

				reminder = new Reminder();
				reminder.setBookmark(bookmark);
				reminder.setTimestamp(date);
				reminder.setEnabled(reminderJSON.getBoolean("enabled"));
				reminder.setTokenFCM(reminderJSON.getString("tokenFCM"));

				DatabaseManager.getInstance().getReminderDao().saveOrUpdate(reminder);
				DatabaseManager.getInstance().commit();
			}
		} catch (SQLException | ParseException e) {
			e.printStackTrace();
		}

		return response.toString();
	}
}
