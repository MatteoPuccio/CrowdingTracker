package com.gpsreminder.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.gpsreminder.model.VenueBusynessLive;
import com.gpsreminder.model.VenueBusynessRaw;
import com.gpsreminder.model.VenueInformation;
import com.gpsreminder.persistence.DatabaseManager;
import com.gpsreminder.utils.Common;

import javafx.util.Pair;

public class BestTimeController {

	private final static String baseUrl = "https://besttime.app/api/v1/";

	private final static String privateKey = "#############";

	public static VenueInformation addVenue(String name, Double lat, Double lng, String address) {
		if (name == null || lat == null || lng == null || address == null)
			return null;

		try {
			VenueInformation venueInformation = DatabaseManager.getInstance().getVenueInformationDao()
					.findByNameAndCoords(name, lat, lng);
			if (venueInformation != null)
				return venueInformation;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		List<Pair<String, String>> params = new ArrayList<Pair<String, String>>();

		// search params
		params.add(new Pair<String, String>("api_key_private", privateKey));
		params.add(new Pair<String, String>("venue_name", name));
		params.add(new Pair<String, String>("venue_address", address));

		HttpPost httpPost = new HttpPost(baseUrl + "forecasts?" + Common.urlSearchParams(params));

		CloseableHttpClient client = HttpClients.createDefault();
		CloseableHttpResponse response;
		VenueInformation venueInfo = null;
		try {
			response = client.execute(httpPost);
			HttpEntity responseEntity = response.getEntity();
			if (responseEntity != null) {
				String responseAsString = EntityUtils.toString(responseEntity);

				System.out.println(responseAsString);

				venueInfo = new VenueInformation();

				venueInfo.setName(name);
				venueInfo.setLatitude(lat);
				venueInfo.setLongitude(lng);
				venueInfo.setAddress(address);

				JSONObject data = new JSONObject(responseAsString);

				if (!data.has("analysis") && data.has("venue_info")) {
					venueInfo.setId(data.getJSONObject("venue_info").getString("venue_id"));
					venueInfo.setAvgDwellTime(Long.valueOf(0));
					DatabaseManager.getInstance().getVenueInformationDao().saveOrUpdate(venueInfo);
					DatabaseManager.getInstance().commit();

					return venueInfo;
				}

				JSONArray analysis = data.getJSONArray("analysis");

				venueInfo.setAvgDwellTime((long) data.getJSONObject("venue_info").getInt("venue_dwell_time_avg"));
				venueInfo.setId(data.getJSONObject("venue_info").getString("venue_id"));

				DatabaseManager.getInstance().getVenueInformationDao().saveOrUpdate(venueInfo);
				DatabaseManager.getInstance().commit();

				for (int i = 0; i < 7; ++i) {
					VenueBusynessRaw busynessRaw = new VenueBusynessRaw();
					busynessRaw.setInfoId(venueInfo.getId());
					busynessRaw.setWeekday(i);

					JSONObject dayAnalysis = new JSONObject(analysis.get(i).toString());
					JSONArray dayRawJSON = dayAnalysis.getJSONArray("day_raw");

					List<Integer> dayRaw = new ArrayList<Integer>();

					for (Object o : dayRawJSON) {
						dayRaw.add((int) o);
					}

					busynessRaw.setBusyness(dayRaw);
					DatabaseManager.getInstance().getVenueBusynessRawDao().saveOrUpdate(busynessRaw);
				}

			}

			client.close();
			response.close();
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}

		return venueInfo;
	}

	public static VenueBusynessLive getVenueBusynessLive(VenueInformation venueInformation) {
		VenueBusynessLive venueBusynessLive = null;

		List<Pair<String, String>> params = new ArrayList<Pair<String, String>>();

		// search params
		params.add(new Pair<String, String>("api_key_private", privateKey));
		params.add(new Pair<String, String>("venue_name", venueInformation.getName()));
		params.add(new Pair<String, String>("venue_address", venueInformation.getAddress()));
		params.add(new Pair<String, String>("venue_id", venueInformation.getId()));

		HttpPost httpPost = new HttpPost(baseUrl + "forecasts/live?" + Common.urlSearchParams(params));

		CloseableHttpClient client = HttpClients.createDefault();
		CloseableHttpResponse response;

		try {
			response = client.execute(httpPost);
			HttpEntity responseEntity = response.getEntity();
			if (responseEntity != null) {
				String responseAsString = EntityUtils.toString(responseEntity);

				System.out.println(responseAsString);

				JSONObject data = new JSONObject(responseAsString);

				if (!data.has("analysis") || !data.has("venue_info"))
					return venueBusynessLive;

				venueBusynessLive = new VenueBusynessLive();

				JSONObject analysis = data.getJSONObject("analysis");

				venueBusynessLive.setForecastedBusyness(
						analysis.has("venue_forecasted_busyness") ? (analysis.getDouble("venue_forecasted_busyness"))
								: 0.0);
				venueBusynessLive.setLiveBusyness(
						analysis.has("venue_live_busyness") ? (analysis.getDouble("venue_live_busyness")) : 0.0);

			}

			client.close();
			response.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return venueBusynessLive;
	}

}
