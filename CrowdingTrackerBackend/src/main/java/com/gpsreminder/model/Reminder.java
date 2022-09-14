package com.gpsreminder.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.json.JSONObject;

public class Reminder {

	private static final String DEFAULT_LANGUAGE = "en";

	private Bookmark bookmark;
	private Timestamp timestamp;
	private Boolean enabled;
	private String tokenFCM, language;

	public Reminder(Bookmark bookmark, String tokenFCM) {
		this.bookmark = bookmark;
		this.tokenFCM = tokenFCM;
		timestamp = new Timestamp((new java.util.Date()).getTime());
		enabled = false;
		language = DEFAULT_LANGUAGE;
	}

	public Reminder(Bookmark bookmark) {
		this.bookmark = bookmark;
		timestamp = new Timestamp((new java.util.Date()).getTime());
		enabled = false;
		language = DEFAULT_LANGUAGE;
	}

	public Reminder() {
		timestamp = new Timestamp((new java.util.Date()).getTime());
		enabled = false;
		language = DEFAULT_LANGUAGE;

	}

	public Bookmark getBookmark() {
		return bookmark;
	}

	public void setBookmark(Bookmark bookmark) {
		this.bookmark = bookmark;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getTokenFCM() {
		return tokenFCM;
	}

	public void setTokenFCM(String tokenFCM) {
		this.tokenFCM = tokenFCM;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Reminder other = (Reminder) obj;
		if (bookmark == null) {
			if (other.bookmark != null)
				return false;
		} else if (!bookmark.equals(other.bookmark))
			return false;
		if (enabled == null) {
			if (other.enabled != null)
				return false;
		} else if (!enabled.equals(other.enabled))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		if (tokenFCM == null) {
			if (other.tokenFCM != null)
				return false;
		} else if (!tokenFCM.equals(other.tokenFCM))
			return false;
		if (language == null) {
			if (other.language != null)
				return false;
		} else if (!language.equals(other.language))
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "Reminder [bookmark=" + bookmark + ", timestamp=" + timestamp + ", enabled=" + enabled + ", tokenFCM="
				+ tokenFCM + ", language=" + language + "]";
	}

	public JSONObject toJSONObject() {
		JSONObject reminderJSON = new JSONObject();

		reminderJSON.put("timestamp", (new SimpleDateFormat("yyyy.MM.dd HH:mm")).format(timestamp));
		reminderJSON.put("enabled", enabled);

		return reminderJSON;
	}

}
