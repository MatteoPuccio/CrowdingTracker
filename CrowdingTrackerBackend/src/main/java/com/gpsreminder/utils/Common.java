package com.gpsreminder.utils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import com.gpsreminder.model.AccessToken;
import com.gpsreminder.persistence.DatabaseManager;

import javafx.util.Pair;

public class Common {

	public static String generateAccessToken() {
		String token = UUID.randomUUID().toString();

		AccessToken accessToken = null;
		try {
			accessToken = DatabaseManager.getInstance().getAccessTokenDao().findByToken(token);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (accessToken != null) // Miniscule chance of UUID collision
			return generateAccessToken();
		return token.toString();
	}

	public static String urlSearchParams(List<Pair<String, String>> params) {
		String url = "";

		for (int i = 0; i < params.size(); ++i) {
			url = url.concat(
					params.get(i).getKey() + "=" + URLEncoder.encode(params.get(i).getValue(), StandardCharsets.UTF_8));
			if (i != params.size() - 1)
				url = url.concat("&");
		}

		return url;
	}

	public static String generateRegistrationToken() {
		return String
				.valueOf(ThreadLocalRandom.current().nextLong((long) Math.pow(10, 7), (long) (Math.pow(10, 8) + 1)));

	}
}
