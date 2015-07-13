package com.alimuzaffar.ramadanalarm.scheduler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.alimuzaffar.ramadanalarm.Constants;
import com.alimuzaffar.ramadanalarm.R;
import com.alimuzaffar.ramadanalarm.RingAlarmActivity;
import com.alimuzaffar.ramadanalarm.util.AppSettings;
import com.alimuzaffar.ramadanalarm.util.PrayTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TimeZone;

/**
 * When the alarm fires, this WakefulBroadcastReceiver receives the broadcast Intent
 * and then starts the IntentService {@code SampleSchedulingService} to do some work.
 */
public class RamadanAlarmReceiver extends WakefulBroadcastReceiver implements Constants {

  // The app's AlarmManager, which provides access to the system alarm services.
  private AlarmManager alarmMgr;
  // The pending intent that is triggered when the alarm fires.
  private PendingIntent sAlarmIntent;
  private PendingIntent iAlarmIntent;

  @Override
  public void onReceive(Context context, Intent intent) {
    // BEGIN_INCLUDE(alarm_onreceive)
        /* 
         * If your receiver intent includes extras that need to be passed along to the
         * service, use setComponent() to indicate that the service should handle the
         * receiver's intent. For example:
         * 
         * ComponentName comp = new ComponentName(context.getPackageName(), 
         *      MyService.class.getName());
         *
         * // This intent passed in this call will include the wake lock extra as well as 
         * // the receiver intent contents.
         * startWakefulService(context, (intent.setComponent(comp)));
         * 
         * In this example, we simply create a new intent to deliver to the service.
         * This intent holds an extra identifying the wake lock.
         */
    String prayerName = intent.getStringExtra(EXTRA_PRAYER_NAME);
    long prayerTime = intent.getLongExtra(EXTRA_PRAYER_TIME, -1);

    boolean timePassed = (prayerTime != -1 && Math.abs(System.currentTimeMillis() - prayerTime) > FIVE_MINUTES);

    AppSettings settings = AppSettings.getInstance(context);
    if (settings.isAlarmSetFor(0)) {
      if (!timePassed) {
        // START THE ALARM ACTIVITY
        Intent newIntent = new Intent(context, RingAlarmActivity.class);
        Log.d("RamadanAlarmReceiver", "Alarm Receiver Got " + prayerName);
        newIntent.putExtra(EXTRA_PRAYER_NAME, prayerName);
        newIntent.putExtra(EXTRA_PRE_ALARM_FLAG, true);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(newIntent);
      }
      //SET THE NEXT ALARM
      setAlarm(context);
    }
  }

  // BEGIN_INCLUDE(set_alarm)

  /**
   * Sets a repeating alarm that runs once a day at approximately 8:30 a.m. When the
   * alarm fires, the app broadcasts an Intent to this WakefulBroadcastReceiver.
   *
   * @param context
   */
  public void setAlarm(Context context) {
    alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

    AppSettings settings = AppSettings.getInstance(context);

    Calendar now = Calendar.getInstance(TimeZone.getDefault());
    now.setTimeInMillis(System.currentTimeMillis());
    // Set the alarm's trigger time to 8:30 a.m.

    double lat = settings.getLatFor(0);
    double lng = settings.getLngFor(0);
    LinkedHashMap<String, String> prayerTimes = PrayTime.getPrayerTimes(context, 0, lat, lng, PrayTime.TIME_24);

    if (!settings.getBoolean(AppSettings.Key.IS_RAMADAN)) {
      return;
    }

    int suhoorOffset = settings.getInt(AppSettings.Key.SUHOOR_OFFSET);
    int iftarOffset = settings.getInt(AppSettings.Key.IFTAR_OFFSET);
    Calendar preSuhoorTime = Calendar.getInstance(TimeZone.getDefault());
    preSuhoorTime.setTimeInMillis(System.currentTimeMillis());
    Calendar preIftarTime = Calendar.getInstance(TimeZone.getDefault());
    preIftarTime.setTimeInMillis(System.currentTimeMillis());
    if (suhoorOffset > 0) {
      long time = suhoorOffset * 15 * 60 * 1000;
      preSuhoorTime = getCalendarFromPrayerTime(preSuhoorTime, prayerTimes.get("Fajr"));
      preSuhoorTime.setTimeInMillis(preSuhoorTime.getTimeInMillis() - time);
    }

    if (iftarOffset > 0) {
      long time = iftarOffset * 15 * 60 * 1000;
      preIftarTime = getCalendarFromPrayerTime(preSuhoorTime, prayerTimes.get("Maghrib"));
      preIftarTime.setTimeInMillis(preIftarTime.getTimeInMillis() - time);
    }

    if (preSuhoorTime.before(now)) {
      preSuhoorTime.add(Calendar.DAY_OF_YEAR, 1);
    }

    if (suhoorOffset > 0) {
      Intent sIntent = new Intent(context, RamadanAlarmReceiver.class);
      sIntent.putExtra(EXTRA_PRAYER_NAME, context.getString(R.string.suhoor_is_close));
      sIntent.putExtra(EXTRA_PRAYER_TIME, preSuhoorTime.getTimeInMillis());
      sAlarmIntent = PendingIntent.getBroadcast(context, PRE_SUHOOR_ALARM_ID, sIntent, PendingIntent.FLAG_CANCEL_CURRENT);
      alarmMgr.set(AlarmManager.RTC_WAKEUP, preSuhoorTime.getTimeInMillis(), sAlarmIntent);
    }

    if (preIftarTime.before(now)) {
      preIftarTime.add(Calendar.DAY_OF_YEAR, 1);
    }

    if (iftarOffset > 0) {
      Intent iIntent = new Intent(context, RamadanAlarmReceiver.class);
      iIntent.putExtra(EXTRA_PRAYER_NAME, context.getString(R.string.iftar_is_close));
      iIntent.putExtra(EXTRA_PRAYER_TIME, preIftarTime.getTimeInMillis());
      iAlarmIntent = PendingIntent.getBroadcast(context, PRE_IFTAR_ALARM_ID, iIntent, PendingIntent.FLAG_CANCEL_CURRENT);
      alarmMgr.set(AlarmManager.RTC_WAKEUP, preIftarTime.getTimeInMillis(), iAlarmIntent);
    }
  }
  // END_INCLUDE(set_alarm)

  /**
   * Cancels the alarm.
   *
   * @param context
   */
  // BEGIN_INCLUDE(cancel_alarm)
  public void cancelAlarm(Context context) {
    // If the alarm has been set, cancel it.
    if (alarmMgr == null) {
      alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }
    if (alarmMgr != null) {
      if (sAlarmIntent == null) {
        Intent sIntent = new Intent(context, RamadanAlarmReceiver.class);
        sAlarmIntent = PendingIntent.getBroadcast(context, PRE_SUHOOR_ALARM_ID, sIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent iIntent = new Intent(context, RamadanAlarmReceiver.class);
        sAlarmIntent = PendingIntent.getBroadcast(context, PRE_SUHOOR_ALARM_ID, iIntent, PendingIntent.FLAG_CANCEL_CURRENT);
      }

      alarmMgr.cancel(sAlarmIntent);
      alarmMgr.cancel(iAlarmIntent);

    }

  }
  // END_INCLUDE(cancel_alarm)


  private Calendar getCalendarFromPrayerTime(Calendar cal, String prayerTime) {
    String[] time = prayerTime.split(":");
    cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time[0]));
    cal.set(Calendar.MINUTE, Integer.valueOf(time[1]));
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);

    return cal;
  }
}
