package com.alimuzaffar.ramadanalarm.scheduler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.alimuzaffar.ramadanalarm.util.AppSettings;

/**
 * This BroadcastReceiver automatically (re)starts the alarm when the device is
 * rebooted. This receiver is set to be disabled (android:enabled="false") in the
 * application's manifest file. When the user sets the alarm, the receiver is enabled.
 * When the user cancels the alarm, the receiver is disabled, so that rebooting the
 * device will not trigger this receiver.
 */
// BEGIN_INCLUDE(autostart)
public class SalaatBootReceiver extends BroadcastReceiver {
  SalaatAlarmReceiver salaatAlarm = new SalaatAlarmReceiver();
  RamadanAlarmReceiver ramadanAlarm = new RamadanAlarmReceiver();

  @Override
  public void onReceive(Context context, Intent intent) {
    String action = intent.getAction();
    if (action.equals("android.intent.action.BOOT_COMPLETED")) {
      if (AppSettings.getInstance(context).isAlarmSetFor(0)) {
        salaatAlarm.setAlarm(context);
      }

      if (AppSettings.getInstance(context).getBoolean(AppSettings.Key.IS_RAMADAN)) {
        ramadanAlarm.setAlarm(context);
      }
    } else if (action.equals("android.intent.action.TIMEZONE_CHANGED") ||
        action.equals("android.intent.action.TIME_SET") ||
        action.equals("android.intent.action.MY_PACKAGE_REPLACED")) {
      // Our location could have changed, which means time calculations may be different
      // now so cancel the alarm and set it again.
      if (AppSettings.getInstance(context).isAlarmSetFor(0)) {
        salaatAlarm.cancelAlarm(context);
        salaatAlarm.setAlarm(context);
      }

      if (AppSettings.getInstance(context).getBoolean(AppSettings.Key.IS_RAMADAN)) {
        ramadanAlarm.cancelAlarm(context);
        ramadanAlarm.setAlarm(context);
      }
    }
  }
}
//END_INCLUDE(autostart)
