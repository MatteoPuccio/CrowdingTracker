package com.gpsreminder.services;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.gpsreminder.controller.BestTimeController;
import com.gpsreminder.model.Bookmark;
import com.gpsreminder.model.Reminder;
import com.gpsreminder.model.VenueBusynessLive;
import com.gpsreminder.model.VenueInformation;
import com.gpsreminder.persistence.DatabaseManager;

@Service
public class ReminderNotificationCreator {

	private static String getTitle() {
		return I18n.getMessage("NotificationTitle");
	}

	private static String getLiveBusynessMessageBody(VenueBusynessLive liveInfo) {
		if (liveInfo.getLiveBusyness() > 0.9)
			return I18n.getMessage("NotificationBodyAlmostFull");
		if (liveInfo.getLiveBusyness() < 0.1)
			return I18n.getMessage("NotificationBodyAlmostEmpty");
		if (liveInfo.getForecastedBusyness() > liveInfo.getLiveBusyness())
			return I18n.getMessage("NotificationBodyLessCrowded");
		return I18n.getMessage("NotificationBodyMoreCrowded");

	}

	@Scheduled(fixedDelay = 10000) // executes the method, then waits 10s
	public void createNotificationTask() {
		try {
			
			
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, 15);
			Timestamp reminderTime = new Timestamp(cal.getTime().getTime()); // send notification 15 minutes before
			
			System.out.println(reminderTime);
			
			List<Reminder> reminders = DatabaseManager.getInstance().getReminderDao()
					.findActivesGreaterThanTimestamp(reminderTime);

			CloseableHttpClient client = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost("https://fcm.googleapis.com/fcm/send");
			httpPost.addHeader("Content-Type", "application/json;charset=UTF-8");
			httpPost.addHeader("Authorization", "key=AAAA_fSvQdg:APA91bHyshAr5jnjIJlULEpYPpZ1WhLPwWqdIQYiC1_izbRduVoMixjcVhw7CMwvmP31jmFXtPxebIpsKrNjOxWLoqGL45eQURhywBnT9ZjUpDus_LMGX2MeKg5tTDcihozzBLvu5Cmw");

			reminders.forEach(reminder -> {
				System.out.println(reminder);
				try {
					Bookmark bookmark = reminder.getBookmark();
					VenueInformation venueInfo = bookmark.getInfo();

					I18n.setLocale(new Locale(reminder.getLanguage()));

					VenueBusynessLive liveInfo = BestTimeController.getVenueBusynessLive(venueInfo);
					
					String json = "{\"notification\": {\"title\": \"" + getTitle() + "\",\"body\": \""
							+ bookmark.getName() + " " + getLiveBusynessMessageBody(liveInfo) + "\"},\"to\": \""
							+ reminder.getTokenFCM() + "\",\"time_to_live\": 0}";

					httpPost.setEntity(new StringEntity(json));
					CloseableHttpResponse response = client.execute(httpPost);

					String responseObjectJson = EntityUtils.toString(response.getEntity());
					
					System.out.println(responseObjectJson);
					
					DatabaseManager.getInstance().getReminderDao().delete(reminder.getBookmark().getId());
				} catch (IOException | ParseException | SQLException e) {
					e.printStackTrace();
				}
			});

			DatabaseManager.getInstance().commit();
			client.close();

		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
	}
}
