package com.gpsreminder.services;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public final class I18n {
	private final static String MESSAGES_KEY = "messages";

	private static ResourceBundle bundle;

	public static Locale getLocale() {
		Locale defaultLocale = Locale.getDefault();
		return defaultLocale;
	}

	public static void setLocale(Locale l) {
		Locale.setDefault(l);
	}

	public static String getMessage(String key) {
		if (bundle == null) {
			bundle = ResourceBundle.getBundle(MESSAGES_KEY);
		}
		return bundle.getString(key);
	}

	public static String getMessage(String key, Object... arguments) {
		return MessageFormat.format(getMessage(key), arguments);
	}

}
