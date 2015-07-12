package com.alimuzaffar.ramadanalarm.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.ArrayAdapter;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Ali on 12/07/2015.
 */
public class AlarmUtils {

  public static Uri getAlarmRingtoneUri() {
    Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
    if (alert == null) {
      // alert is null, using backup
      alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
      if (alert == null) { // I can't see this ever being null (as always
        // have a default notification) but just incase
        // alert backup is null, using 2nd backup
        alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
      }
    }
    return alert;
  }

  public static int getAlarmVolumeFromPercentage(AudioManager audioManager, float percentage) {
    int volume = (int) Math.ceil((double) audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM) * (percentage / 100.0d));
    return volume;
  }

  public static Map<String, Uri> getRingtones(Activity activity) {
    RingtoneManager manager = new RingtoneManager(activity);
    manager.setType(RingtoneManager.TYPE_RINGTONE);
    Cursor cursor = manager.getCursor();

    Map<String, Uri> list = new LinkedHashMap<>();
    while (cursor.moveToNext()) {
      String notificationTitle = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
      Uri notificationUri = manager.getRingtoneUri(cursor.getPosition());

      list.put(notificationTitle, notificationUri);
    }

    return list;
  }

  public static void getRingtonesDialog(Activity activity, Collection<String> items, int selected, DialogInterface.OnClickListener itemClickListener, DialogInterface.OnClickListener okClickListener, DialogInterface.OnClickListener cancelClickListener) {
    AlertDialog.Builder builderSingle = new AlertDialog.Builder(activity);
    builderSingle.setTitle("Select Ringtone");
    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(activity, android.R.layout.select_dialog_singlechoice);
    arrayAdapter.addAll(items);

    builderSingle.setNegativeButton(android.R.string.cancel, cancelClickListener);

    builderSingle.setPositiveButton(android.R.string.ok, okClickListener);

    builderSingle.setSingleChoiceItems(arrayAdapter, selected, itemClickListener);
    builderSingle.show();
  }
}
