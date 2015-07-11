package com.alimuzaffar.ramadanalarm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public interface Constants {
  // REQUEST CODES
  int REQUEST_CHECK_SETTINGS = 101;
  int REQUEST_ONBOARDING = 102;
  int REQUEST_LOCATION = 103;
  int REQUEST_WRITE_EXTERNAL = 104;
  int REQUEST_SET_ALARM = 105;

  final int ALARM_ID = 1010;

  long ONE_MINUTE = 60000;
  long FIVE_MINUTES = ONE_MINUTE * 5;

  //EXTRAS
  String EXTRA_ALARM_INDEX = "alarm_index";
  String EXTRA_LAST_LOCATION = "last_location";
  String EXTRA_PRAYER_NAME = "prayer_name";

  String CONTENT_FRAGMENT = "content_fragment";
  String TIMES_FRAGMENT = "times_fragment";
  String CONFIG_FRAGMENT = "config_fragment";
  String LOCATION_FRAGMENT = "location_fragment";


  public static final DateFormat TIME = new SimpleDateFormat("HH:mm", Locale.getDefault());

}
