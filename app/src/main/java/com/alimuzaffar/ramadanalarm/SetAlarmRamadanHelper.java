package com.alimuzaffar.ramadanalarm;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

import com.alimuzaffar.ramadanalarm.util.AppSettings;

/**
 * Created by Ali on 12/07/2015.
 */
public class SetAlarmRamadanHelper implements CompoundButton.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {
  AppSettings mSettings;

  SetAlarmActivity mActivity;
  int mIndex = 0;

  CheckBox mRamadan;
  Spinner mSuhoor;
  Spinner mIftar;
  ViewGroup mSuhoorGroup;
  ViewGroup mIftarGroup;

  public SetAlarmRamadanHelper(SetAlarmActivity activity, int alarmIndex) {
    mActivity = activity;
    mIndex = alarmIndex;
    mSettings = AppSettings.getInstance(activity);

    mRamadan = (CheckBox) mActivity.findViewById(R.id.ramadan);
    mSuhoor = (Spinner) mActivity.findViewById(R.id.suhoor_offset);
    mIftar = (Spinner) mActivity.findViewById(R.id.iftar_offset);
    mSuhoorGroup = (ViewGroup) mActivity.findViewById(R.id.suhoor_alarm);
    mIftarGroup = (ViewGroup) mActivity.findViewById(R.id.iftar_alarm);

    init();
  }

  public void init() {
    mRamadan.setOnCheckedChangeListener(this);
    mRamadan.setChecked(mSettings.getBoolean(AppSettings.Key.IS_RAMADAN));

    mSuhoor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mSettings.set(AppSettings.Key.SUHOOR_OFFSET, position);
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
      }
    });

    mIftar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mSettings.set(AppSettings.Key.IFTAR_OFFSET, position);
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
      }
    });

  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    mSettings.set(AppSettings.Key.IS_RAMADAN, isChecked);

    mSuhoorGroup.setVisibility(isChecked? View.VISIBLE : View.GONE);
    mIftarGroup.setVisibility(isChecked? View.VISIBLE : View.GONE);

    if (isChecked) {
      mSuhoor.setSelection(mSettings.getInt(AppSettings.Key.SUHOOR_OFFSET));
      mIftar.setSelection(mSettings.getInt(AppSettings.Key.IFTAR_OFFSET));
    } else {
      mSettings.set(AppSettings.Key.SUHOOR_OFFSET, 0);
      mSettings.set(AppSettings.Key.IFTAR_OFFSET, 0);
    }

  }

  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) {

  }
}
