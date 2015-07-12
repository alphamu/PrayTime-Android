package com.alimuzaffar.ramadanalarm.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.ArrayAdapter;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Ali on 12/07/2015.
 */
public class ScreenUtils {
  /**
   * Locks the device window in actual screen mode.
   */
  public static void lockOrientation(Activity activity) {
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
  }

  /**
   * Unlocks the device window in user defined screen mode.
   */
  public static void unlockOrientation(Activity activity) {
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
  }

}
