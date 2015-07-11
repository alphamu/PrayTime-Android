package com.alimuzaffar.ramadanalarm.scheduler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.alimuzaffar.ramadanalarm.Constants;
import com.alimuzaffar.ramadanalarm.R;
import com.alimuzaffar.ramadanalarm.RingAlarmActivity;
import com.alimuzaffar.ramadanalarm.util.AppSettings;
import com.alimuzaffar.ramadanalarm.util.PrayTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TimeZone;

/**
 * When the alarm fires, this WakefulBroadcastReceiver receives the broadcast Intent
 * and then starts the IntentService {@code SampleSchedulingService} to do some work.
 */
public class SalaatAlarmReceiver extends WakefulBroadcastReceiver implements Constants {

  // The app's AlarmManager, which provides access to the system alarm services.
  private AlarmManager alarmMgr;
  // The pending intent that is triggered when the alarm fires.
  private PendingIntent alarmIntent;

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
    AppSettings settings = AppSettings.getInstance(context);
    if (settings.isAlarmSetFor(0)) {
      Intent service = new Intent(context, SalaatSchedulingService.class);
      service.putExtra(EXTRA_PRAYER_NAME, prayerName);

      // Start the service, keeping the device awake while it is launching.
      startWakefulService(context, service);
      // END_INCLUDE(alarm_onreceive)

      // START THE ALARM ACTIVITY
      Intent newIntent = new Intent(context, RingAlarmActivity.class);
      Log.d("SalaatAlarmReceiver", "Alarm Receiver Got " + prayerName);
      newIntent.putExtra(EXTRA_PRAYER_NAME, prayerName);
      newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(newIntent);

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
    Intent intent = new Intent(context, SalaatAlarmReceiver.class);

    Calendar now = Calendar.getInstance(TimeZone.getDefault());
    now.setTimeInMillis(System.currentTimeMillis());
    // Set the alarm's trigger time to 8:30 a.m.

    int alarmIndex = 0;

    Calendar then = Calendar.getInstance(TimeZone.getDefault());
    then.setTimeInMillis(System.currentTimeMillis());

    double lat = AppSettings.getInstance(context).getLatFor(alarmIndex);
    double lng = AppSettings.getInstance(context).getLngFor(alarmIndex);
    LinkedHashMap<String, String> prayerTimes = PrayTime.getPrayerTimes(context, alarmIndex, lat, lng, PrayTime.TIME_24);

    AppSettings settings = AppSettings.getInstance(context);

    boolean nextAlarmFound = false;
    String nameOfPrayerFound = null;
    for (String prayer : prayerTimes.keySet()) {
      if (!isAlarmEnabledForPrayer(settings, prayer, alarmIndex)) {
        continue;
      }

      then = getCalendarFromPrayerTime(then, prayerTimes.get(prayer));

      if (then.after(now)) {
        // this is the alarm to set
        nameOfPrayerFound = prayer;
        nextAlarmFound = true;
        break;
      }
    }

    if (!nextAlarmFound) {
      for (String prayer : prayerTimes.keySet()) {
        if (!isAlarmEnabledForPrayer(settings, prayer, alarmIndex)) {
          continue;
        }

        then = getCalendarFromPrayerTime(then, prayerTimes.get(prayer));

        if (then.before(now)) {
          // this is the next day.
          nameOfPrayerFound = prayer;
          nextAlarmFound = true;
          then.add(Calendar.DAY_OF_YEAR, 1);
          break;
        }
      }
    }

    if (!nextAlarmFound) {
      return; //something went wrong, abort!
    }

    nameOfPrayerFound = getPrayerNameFromIndex(context, getPrayerIndexFromName(nameOfPrayerFound));
    intent.putExtra(EXTRA_PRAYER_NAME, nameOfPrayerFound);
    alarmIntent = PendingIntent.getBroadcast(context, ALARM_ID, intent, 0);

    alarmMgr.set(AlarmManager.RTC_WAKEUP, then.getTimeInMillis(), alarmIntent);

    // Enable {@code SampleBootReceiver} to automatically restart the alarm when the
    // device is rebooted.
    ComponentName receiver = new ComponentName(context, SalaatBootReceiver.class);
    PackageManager pm = context.getPackageManager();

    pm.setComponentEnabledSetting(receiver,
        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
        PackageManager.DONT_KILL_APP);
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
    if (alarmMgr != null) {
      if (alarmIntent == null) {
        Intent intent = new Intent(context, SalaatAlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, ALARM_ID, intent, 0);
      }
      alarmMgr.cancel(alarmIntent);
    }

    // Disable {@code SampleBootReceiver} so that it doesn't automatically restart the
    // alarm when the device is rebooted.
    ComponentName receiver = new ComponentName(context, SalaatBootReceiver.class);
    PackageManager pm = context.getPackageManager();

    pm.setComponentEnabledSetting(receiver,
        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
        PackageManager.DONT_KILL_APP);
  }
  // END_INCLUDE(cancel_alarm)

  private Date getDateFromString(String timeStr) {
    try {
      Date time = TIME.parse(timeStr);
      return time;
    } catch (ParseException pe) {
      Log.e("SalaatAlarmReceiver", "ERROR PARSING TIME");
      return null;
    }
  }

  private int getPrayerIndexFromName(String prayerName) {
    String name = prayerName.toLowerCase();
    char index = name.charAt(0);
    switch (index) {
      case 'f':
        return 0;
      case 'd':
        return 1;
      case 'a':
        return 2;
      case 'm':
        return 3;
      case 'i':
        return 4;
    }
    return -1;
  }

  private String getPrayerKeyFromIndex(AppSettings settings, int prayerIndex, int index) {
    String key = null;
    switch (prayerIndex) {
      case 0:
        key = settings.getKeyFor(AppSettings.Key.IS_FAJR_ALARM_SET, index);
        break;
      case 1:
        key = settings.getKeyFor(AppSettings.Key.IS_DHUHR_ALARM_SET, index);
        break;
      case 2:
        key = settings.getKeyFor(AppSettings.Key.IS_ASR_ALARM_SET, index);
        break;
      case 3:
        key = settings.getKeyFor(AppSettings.Key.IS_MAGHRIB_ALARM_SET, index);
        break;
      case 4:
        key = settings.getKeyFor(AppSettings.Key.IS_ISHA_ALARM_SET, index);
        break;
    }
    return key;
  }

  private String getPrayerNameFromIndex(Context context, int prayerIndex) {
    String prayerName = null;
    switch (prayerIndex) {
      case 0:
        prayerName = context.getString(R.string.fajr);
        break;
      case 1:
        prayerName = context.getString(R.string.dhuhr);
        break;
      case 2:
        prayerName = context.getString(R.string.asr);
        break;
      case 3:
        prayerName = context.getString(R.string.maghrib);
        break;
      case 4:
        prayerName = context.getString(R.string.isha);
        break;
    }
    return prayerName;
  }

  private boolean isAlarmEnabledForPrayer(AppSettings settings, String prayer, int alarmIndex) {
    if (prayer.equalsIgnoreCase("sunrise") || prayer.equalsIgnoreCase("sunset")) {
      return false;
    }

    int prayerIndex = getPrayerIndexFromName(prayer);
    String key = getPrayerKeyFromIndex(settings, prayerIndex, alarmIndex);
    return settings.getBoolean(key);
  }

  private Calendar getCalendarFromPrayerTime(Calendar cal, String prayerTime) {
    String[] time =prayerTime.split(":");
    cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time[0]));
    cal.set(Calendar.MINUTE, Integer.valueOf(time[1]));
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    return cal;
  }
}
