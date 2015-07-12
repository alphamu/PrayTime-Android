package com.alimuzaffar.ramadanalarm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.alimuzaffar.ramadanalarm.util.AppSettings;

public class SetAlarmActivity extends AppCompatActivity implements Constants,
    CompoundButton.OnCheckedChangeListener, View.OnClickListener {

  int mIndex = 0;
  AppSettings settings;
  CheckBox mAlarm;
  TextView[] mPrayers = new TextView[5];

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_set_alarm);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    mAlarm = (CheckBox) findViewById(R.id.alarm);

    mPrayers[0] = (TextView) findViewById(R.id.fajr);
    mPrayers[1] = (TextView) findViewById(R.id.dhuhr);
    mPrayers[2] = (TextView) findViewById(R.id.asr);
    mPrayers[3] = (TextView) findViewById(R.id.maghrib);
    mPrayers[4] = (TextView) findViewById(R.id.isha);

    for (int i = 0; i < mPrayers.length; i++) {
      TextView tv = mPrayers[i];
      tv.setOnClickListener(this);
      tv.setTag(new Integer(i));
    }

    Intent intent = getIntent();
    if (intent != null && intent.hasExtra(EXTRA_ALARM_INDEX)) {
      mIndex = intent.getIntExtra(EXTRA_ALARM_INDEX, 0);
    }

    settings = AppSettings.getInstance(this);

    // Init the alarm settings
    if (settings.isAlarmSetFor(mIndex)) {
      mAlarm.setChecked(true);
      for (int i = 0; i < mPrayers.length; i++) {
        TextView tv = mPrayers[i];
        tv.setEnabled(true);
        boolean status = getPrayerAlarmStatus(i);
        tv.setSelected(status);
      }
    }
    //attach listener after init to prevent unpredictable result.
    mAlarm.setOnCheckedChangeListener(this);

  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    settings.setAlarmFor(mIndex, isChecked);
    for (int i = 0; i < mPrayers.length; i++) {
      TextView tv = mPrayers[i];
      tv.setEnabled(isChecked);
      tv.setSelected(isChecked);
      setPrayerAlarmStatus(i, isChecked);
    }
  }

  @Override
  public void onClick(View v) {
    int index = (Integer) v.getTag();
    boolean isSet = getPrayerAlarmStatus(index);
    setPrayerAlarmStatus(index, !isSet);
    v.setSelected(!isSet);
  }

  private void setPrayerAlarmStatus(int prayerIndex, boolean isOn) {
    String key = getPrayerKeyFromIndex(prayerIndex);

    if (key != null) {
      settings.set(key, isOn);
    }
  }

  private boolean getPrayerAlarmStatus(int prayerIndex) {
    String key = getPrayerKeyFromIndex(prayerIndex);

    if (key != null) {
      return settings.getBoolean(key);
    }
    return false;
  }

  private String getPrayerKeyFromIndex(int prayerIndex) {
    String key = null;
    switch (prayerIndex) {
      case 0:
        key = settings.getKeyFor(AppSettings.Key.IS_FAJR_ALARM_SET, mIndex);
        break;
      case 1:
        key = settings.getKeyFor(AppSettings.Key.IS_DHUHR_ALARM_SET, mIndex);
        break;
      case 2:
        key = settings.getKeyFor(AppSettings.Key.IS_ASR_ALARM_SET, mIndex);
        break;
      case 3:
        key = settings.getKeyFor(AppSettings.Key.IS_MAGHRIB_ALARM_SET, mIndex);
        break;
      case 4:
        key = settings.getKeyFor(AppSettings.Key.IS_ISHA_ALARM_SET, mIndex);
        break;
    }
    return key;
  }

  @Override
  public void onBackPressed() {
    boolean anyAlarmSelected = false;
    for (TextView tv : mPrayers) {
      if (tv.isSelected()) {
        anyAlarmSelected = true;
      }
    }

    if (!anyAlarmSelected) {
      mAlarm.setChecked(false);
    }

    Intent data = new Intent();
    if (getParent() == null) {
      setResult(RESULT_OK, data);
    } else {
      getParent().setResult(RESULT_OK, data);
    }

    super.onBackPressed();
  }
}
