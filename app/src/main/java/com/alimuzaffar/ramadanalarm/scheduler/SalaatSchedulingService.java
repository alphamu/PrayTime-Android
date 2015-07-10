package com.alimuzaffar.ramadanalarm.scheduler;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.alimuzaffar.ramadanalarm.Constants;
import com.alimuzaffar.ramadanalarm.R;
import com.alimuzaffar.ramadanalarm.SalaatTimesActivity;
import com.alimuzaffar.ramadanalarm.util.AppSettings;
import com.alimuzaffar.ramadanalarm.util.PrayTime;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.TimeZone;

/**
 * This {@code IntentService} does the app's actual work.
 * {@code SampleAlarmReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class SalaatSchedulingService extends IntentService implements Constants {
  public SalaatSchedulingService() {
    super("SchedulingService");
  }

  public static final String TAG = "Scheduling Demo";
  // An ID used to post the notification.
  public static final int NOTIFICATION_ID = 1;
  // The Google home page URL from which the app fetches content.
  // You can find a list of other Google domains with possible doodles here:
  // http://en.wikipedia.org/wiki/List_of_Google_domains
  private NotificationManager mNotificationManager;
  NotificationCompat.Builder builder;

  @Override
  protected void onHandleIntent(Intent intent) {
    // BEGIN_INCLUDE(service_onhandle)

    String prayerName = "";

    double lat = AppSettings.getInstance(getApplicationContext()).getLatFor(0);
    double lng = AppSettings.getInstance(getApplicationContext()).getLngFor(0);
    LinkedHashMap<String, String> prayerTimes = PrayTime.getPrayerTimes(getApplicationContext(), 0, lat, lng, PrayTime.TIME_24);

    Calendar now = Calendar.getInstance(TimeZone.getDefault());
    now.setTimeInMillis(System.currentTimeMillis());

    Calendar alarm = Calendar.getInstance(TimeZone.getDefault());
    for (String prayer : prayerTimes.keySet()) {
      String[] time = prayerTimes.get(prayer).split(":");
      alarm.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time[0]));
      alarm.set(Calendar.MINUTE, Integer.valueOf(time[1]));
      alarm.set(Calendar.SECOND, 0);
      alarm.set(Calendar.MILLISECOND, 0);

      //if within 5 minutes of the prayer time
      if (Math.abs(alarm.getTimeInMillis() - now.getTimeInMillis()) < (60000 * 5)) {
        prayerName = prayer;
        break;
      }
    }

    sendNotification(String.format("%2$tl:%2$tM %1$s time", prayerName, now), "This is a test notification for " + prayerName);
    // Release the wake lock provided by the BroadcastReceiver.
    SalaatAlarmReceiver.completeWakefulIntent(intent);
    // END_INCLUDE(service_onhandle)
  }

  // Post a notification indicating whether a doodle was found.
  private void sendNotification(String title, String msg) {
    mNotificationManager = (NotificationManager)
        this.getSystemService(Context.NOTIFICATION_SERVICE);

    PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
        new Intent(this, SalaatTimesActivity.class), 0);

    NotificationCompat.Builder mBuilder =
        new NotificationCompat.Builder(this)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setStyle(new NotificationCompat.BigTextStyle()
                .bigText(msg))
            .setContentText(msg);

    mBuilder.setContentIntent(contentIntent);
    mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
  }

}
