package com.alimuzaffar.ramadanalarm.scheduler;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.alimuzaffar.ramadanalarm.BaseActivity;
import com.alimuzaffar.ramadanalarm.R;

/**
 * This {@code IntentService} does the app's actual work.
 * {@code SampleAlarmReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class SalaatSchedulingService extends IntentService {
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

    sendNotification("", "");
    // Release the wake lock provided by the BroadcastReceiver.
    SalaatAlarmReceiver.completeWakefulIntent(intent);
    // END_INCLUDE(service_onhandle)
  }

  // Post a notification indicating whether a doodle was found.
  private void sendNotification(String title, String msg) {
    mNotificationManager = (NotificationManager)
        this.getSystemService(Context.NOTIFICATION_SERVICE);

    PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
        new Intent(this, BaseActivity.class), 0);

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
