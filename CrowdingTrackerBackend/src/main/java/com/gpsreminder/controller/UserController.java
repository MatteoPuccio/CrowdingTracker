package com.gpsreminder.controller;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gpsreminder.model.AccessToken;
import com.gpsreminder.model.RegistrationToken;
import com.gpsreminder.model.User;
import com.gpsreminder.persistence.DatabaseManager;
import com.gpsreminder.services.EmailService;
import com.gpsreminder.utils.Common;

@RestController
public class UserController {

	@PostMapping("/doLogin")
	public String login(@RequestBody ObjectNode json) {
		JSONObject response = new JSONObject();
		if (json.get("email") == null || json.get("password") == null) {
			response.put("missingParams", true);
			return response.toString();
		}
		String email = json.get("email").asText();
		String password = json.get("password").asText();

		try {
			User user = DatabaseManager.getInstance().getUserDao().findByEmail(email);
			if (user == null) {
				response.put("userNotFound", true);
			} else if (!BCrypt.checkpw(password, user.getHashedPassword())) {
				response.put("wrongPassword", true);
			} else {
				JSONObject userJSON = new JSONObject();
				AccessToken accessToken = new AccessToken(user);

				DatabaseManager.getInstance().getAccessTokenDao().saveOrUpdate(accessToken);
				DatabaseManager.getInstance().commit();

				userJSON.put("accessToken", accessToken.getToken());
				userJSON.put("userId", user.getId());
				response.put("user", userJSON);
			}
		} catch (SQLException | JSONException e) {
			e.printStackTrace();
		}

		return response.toString();
	}

	@PostMapping("/tokenLogin")
	public String tokenLogin(@RequestBody ObjectNode json) {
		JSONObject response = new JSONObject();

		if (json.get("token") == null || json.get("userId") == null) {
			response.put("missingParams", true);
			return response.toString();
		}
		String tokenString = json.get("token").asText();
		Long userId = json.get("userId").asLong();

		try {
			User user = DatabaseManager.getInstance().getUserDao().findByPrimaryKey(userId);
			if (user == null) {
				response.put("userNotFound", true);
				return response.toString();
			}
			AccessToken token = DatabaseManager.getInstance().getAccessTokenDao().findByUser(user);
			if (token == null) {
				System.out.println("null");
				response.put("tokenNotFound", true);
			} else if (token.isExpired()) {
				response.put("tokenExpired", true);
			} else if (!token.getToken().equals(tokenString)) {
				response.put("tokenIdMismatch", true);
			} else {
				JSONObject userJSON = new JSONObject();
				userJSON.put("userId", user.getId());

				response.put("user", userJSON);
			}
		} catch (SQLException | JSONException e) {
			e.printStackTrace();
		}

		System.out.println(response.toString());
		return response.toString();
	}

	@PostMapping("/doSignUp")
	public String signUp(@RequestBody ObjectNode json) {
		JSONObject response = new JSONObject();
		if (json.get("email") == null || json.get("password") == null || json.get("locale") == null) {
			response.put("missingParams", true);
			return response.toString();
		}
		String email = json.get("email").asText();
		String password = json.get("password").asText();

		try {
			User conflictingUser = DatabaseManager.getInstance().getUserDao().findByEmail(email);
			if (conflictingUser != null) {
				response.put(conflictingUser.getEmailConfirmed() ? "emailAlreadyUsed" : "emailNotConfirmed", true);
			} else {
				String cryptedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));

				User user = new User();
				user.setHashedPassword(cryptedPassword);
				user.setEmail(email);
				user.setEmailConfirmed(false);
				user.setId(DatabaseManager.getInstance().getIdBroker().getNextUserId());

				RegistrationToken token = new RegistrationToken();
				token.setToken(Common.generateRegistrationToken());
				token.setUser(user);

				Locale locale = new Locale(json.get("locale").asText());

				EmailService.getInstance().sendRegistrationTokenEmail(token.getToken().toString(), email, locale);

				DatabaseManager.getInstance().getUserDao().saveOrUpdate(user);
				DatabaseManager.getInstance().getRegistrationTokenDao().saveOrUpdate(token);
				DatabaseManager.getInstance().commit();

				System.out.println(user);

				response.put("successful", true);
			}
		} catch (SQLException | JSONException e) {
			e.printStackTrace();
		}

		return response.toString();
	}

	@PostMapping("/doConfirmEmail")
	public String confirmEmail(@RequestBody ObjectNode json) {
		JSONObject response = new JSONObject();
		if (json.get("email") == null || json.get("token") == null || json.get("locale") == null) {
			response.put("missingParams", true);
			return response.toString();
		}
		String email = json.get("email").asText();
		String token = json.get("token").asText();
		Locale locale = new Locale(json.get("locale").asText());

		System.out.println(email + " " + token);
		
		try {
			User user = DatabaseManager.getInstance().getUserDao().findByEmail(email);
			RegistrationToken registrationToken = DatabaseManager.getInstance().getRegistrationTokenDao()
					.getUserRegistrationToken(user);
			if(user == null) 
				response.put("userNotFound", true);
			else if (user.getEmailConfirmed())
				response.put("emailAlreadyConfirmed", true);
			else if (!registrationToken.getToken().equals(token))
				response.put("wrongCode", true);
			else if (registrationToken.getExpirationDate().before(Calendar.getInstance().getTime())) {
				System.out.println("here?" + registrationToken.getExpirationDate() + " " + Calendar.getInstance().getTime());
				response.put("expiredCode", true);
				
				RegistrationToken newToken = new RegistrationToken();
				newToken.setUser(user);
				newToken.setToken(Common.generateRegistrationToken());
				
				DatabaseManager.getInstance().getRegistrationTokenDao().saveOrUpdate(newToken);
				
				EmailService.getInstance().sendRegistrationTokenEmail(newToken.getToken(), email,
						locale);
			} else {
				response.put("successful", true);
				
				user.setEmailConfirmed(true);
				DatabaseManager.getInstance().getUserDao().saveOrUpdate(user);
				DatabaseManager.getInstance().getRegistrationTokenDao().delete(user);
				DatabaseManager.getInstance().commit();
			}
		} catch (SQLException | JSONException e) {
			e.printStackTrace();
		}

		return response.toString();
	}

	@PostMapping("/sendConfirmEmailAgain")
	public String sendConfirmEmailAgain(@RequestBody ObjectNode json) {
		JSONObject response = new JSONObject();
		if (json.get("email") == null || json.get("locale") == null) {
			response.put("missingParams", true);
			return response.toString();
		}
		String email = json.get("email").asText();
		Locale locale = new Locale(json.get("locale").asText());

		try {
			User user = DatabaseManager.getInstance().getUserDao().findByEmail(email);
			if(user == null)
				response.put("userNotFound", true);
			else if (user.getEmailConfirmed()) {
				response.put("emailAlreadyConfirmed", true);
			} else {
			
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.WEEK_OF_YEAR, 1);
				Timestamp currentTime = new Timestamp(cal.getTime().getTime());

				RegistrationToken token = new RegistrationToken();
				token.setToken(Common.generateRegistrationToken());
				token.setUser(user);
				token.setExpirationDate(currentTime);

				EmailService.getInstance().sendRegistrationTokenEmail(token.getToken().toString(), email, locale);

				DatabaseManager.getInstance().getRegistrationTokenDao().saveOrUpdate(token);
				DatabaseManager.getInstance().commit();

				response.put("successful", true);
			}
		} catch (SQLException | JSONException e) {
			e.printStackTrace();
		}

		return response.toString();
	}
}
