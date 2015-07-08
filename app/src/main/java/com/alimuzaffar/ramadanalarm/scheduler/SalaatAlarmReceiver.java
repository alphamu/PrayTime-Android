package com.alimuzaffar.ramadanalarm.scheduler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.WakefulBroadcastReceiver;

import java.util.Calendar;

/**
 * When the alarm fires, this WakefulBroadcastReceiver receives the broadcast Intent
 * and then starts the IntentService {@code SampleSchedulingService} to do some work.
 */
public class SalaatAlarmReceiver extends WakefulBroadcastReceiver {
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
    Intent service = new Intent(context, SalaatSchedulingService.class);

    // Start the service, keeping the device awake while it is launching.
    startWakefulService(context, service);
    // END_INCLUDE(alarm_onreceive)

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
    alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(System.currentTimeMillis());
    // Set the alarm's trigger time to 8:30 a.m.
    calendar.set(Calendar.HOUR_OF_DAY, 8);
    calendar.set(Calendar.MINUTE, 30);

    // Set the alarm to fire at approximately 8:30 a.m., according to the device's
    // clock, and to repeat once a day.
    alarmMgr.set(AlarmManager.RTC_WAKEUP,
        calendar.getTimeInMillis(), alarmIntent);

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
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
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
}
