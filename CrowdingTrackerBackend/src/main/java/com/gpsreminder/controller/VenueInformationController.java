package com.gpsreminder.controller;

import java.sql.SQLException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gpsreminder.model.AccessToken;
import com.gpsreminder.model.Bookmark;
import com.gpsreminder.model.VenueBusynessRaw;
import com.gpsreminder.model.VenueInformation;
import com.gpsreminder.persistence.DatabaseManager;

@RestController
public class VenueInformationController {

	@PostMapping("/getBusynessFromBookmark")
	public String getVenueBusynessFromBookmark(@RequestBody ObjectNode json) {
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
				VenueInformation venueInformation = bookmark.getInfo();

				List<VenueBusynessRaw> venueBusynessRawList = DatabaseManager.getInstance().getVenueBusynessRawDao()
						.findByVenueInfoId(venueInformation.getId());
				
				if(venueBusynessRawList.isEmpty()) {
					response.put("noData", "No Data about the location");
					return response.toString();
				}
				
				JSONArray venueBusynessJSONArray = new JSONArray();
				
				for (VenueBusynessRaw busyness : venueBusynessRawList) {
					JSONObject busynessJSON = new JSONObject();
					busynessJSON.put(((Integer)busyness.getWeekday()).toString(), busyness.getBusyness());
					venueBusynessJSONArray.put(busynessJSON);
				}
				
				response.put("busyness", venueBusynessJSONArray);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return response.toString();
	}
	
	@PostMapping("/getInfoFromBookmark")
	public String getBookmarkInfo(@RequestBody ObjectNode json) {
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
				VenueInformation venueInformation = bookmark.getInfo();
				
				JSONObject venueInfo = new JSONObject();
				
				venueInfo.put("avgDwellTime", venueInformation.getAvgDwellTime());
				
				response.put("info", venueInfo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println(response.toString());
		return response.toString();
	}
}
