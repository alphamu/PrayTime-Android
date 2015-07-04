package com.alimuzaffar.ramadanalarm;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alimuzaffar.ramadanalarm.fragments.LocationHelper;
import com.alimuzaffar.ramadanalarm.utils.PrayTime;

import java.util.LinkedHashMap;
import java.util.TimeZone;


public class SalaatTimesActivity extends BaseActivity implements Constants, View.OnClickListener {

  ViewGroup mTimesContainer;
  TextView mConfigureNow;
  TextView mUseDefault;

  int settingsRetries = 0;

  private LocationHelper mLocationHelper;
  private Location mLastLocation = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_salaat_times);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    LayoutInflater inflater = LayoutInflater.from(this);
    mTimesContainer = (ViewGroup) findViewById(R.id.times_container);

    if (!AppSettings.getInstance(this).getBoolean(AppSettings.Key.HAS_DEFAULT_SET)) {
      mTimesContainer.removeAllViews();
      View configure = inflater.inflate(R.layout.view_configure_app, mTimesContainer, true);
      mConfigureNow = (TextView) configure.findViewById(R.id.configure_now);
      mUseDefault = (TextView) configure.findViewById(R.id.use_default);

      mConfigureNow.setOnClickListener(this);
      mUseDefault.setOnClickListener(this);
    }

    mLocationHelper =
        (LocationHelper) getSupportFragmentManager().findFragmentByTag("location_fragment");

    if(mLocationHelper == null) {
      mLocationHelper = new LocationHelper();
      getSupportFragmentManager().beginTransaction()
          .add(mLocationHelper, "location_fragment").commit();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
/*    if (BuildConfig.DEBUG) {
      double latitude = -33.8736779;
      double longitude = 151.196515;
      mLastLocation = new Location("gps");
      mLastLocation.setLatitude(latitude);
      mLastLocation.setLongitude(longitude);
      init();
    }
*/
    if (mLastLocation == null) {
      fetchLocation();
    }
  }

  protected void init() {
    // In future releases we will add more cards.
    // Then we'll need to do this for each card.
    // For now it's included in the layout which
    // makes it easier to work with the layout editor.
    // inflater.inflate(R.layout.view_prayer_times, timesContainer, true);

    //Toolbar will now take on default Action Bar characteristics
    LinkedHashMap<String, String> prayerTimes = PrayTime.getPrayerTimes(this, mLastLocation.getLatitude(), mLastLocation.getLongitude());

    TextView title = (TextView) findViewById(R.id.card_title);
    title.setText(TimeZone.getDefault().getID());

    TextView fajr = (TextView) findViewById(R.id.fajr);
    TextView dhuhr = (TextView) findViewById(R.id.dhuhr);
    TextView asr = (TextView) findViewById(R.id.asr);
    TextView maghrib = (TextView) findViewById(R.id.maghrib);
    TextView isha = (TextView) findViewById(R.id.isha);
    TextView sunrise = (TextView) findViewById(R.id.sunrise);
    TextView sunset = (TextView) findViewById(R.id.sunset);
    TextView alarm = (TextView) findViewById(R.id.alarm);

    fajr.setText(prayerTimes.get(String.valueOf(fajr.getTag())));
    dhuhr.setText(prayerTimes.get(String.valueOf(dhuhr.getTag())));
    asr.setText(prayerTimes.get(String.valueOf(asr.getTag())));
    maghrib.setText(prayerTimes.get(String.valueOf(maghrib.getTag())));
    isha.setText(prayerTimes.get(String.valueOf(isha.getTag())));
    sunrise.setText(prayerTimes.get(String.valueOf(sunrise.getTag())));
    sunset.setText(prayerTimes.get(String.valueOf(sunset.getTag())));

    //set text for the first card.
    setAlarmButtonText(alarm, 0);
    setAlarmButtonClickListener(alarm, 0);
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_salaat_times, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      startOnboardingFor(0);
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private void setAlarmButtonText(TextView button, int index) {
    boolean isAlarmSet = AppSettings.getInstance(this).isAlarmSetFor(index);
    int isAlarmSetInt = isAlarmSet ? 0 : 1;
    String buttonText = getResources().getQuantityString(R.plurals.button_alarm, isAlarmSetInt);
    button.setText(buttonText);
  }

  private void setAlarmButtonClickListener(TextView alarm, int index) {
    alarm.setOnClickListener(new View.OnClickListener() {
      int index = 0;

      @Override
      public void onClick(View v) {
        Intent intent = new Intent(SalaatTimesActivity.this, SetAlarmActivity.class);
        intent.putExtra(EXTRA_ALARM_INDEX, index);
        startActivity(intent);
      }

      public View.OnClickListener init(int index) {
        this.index = index;
        return this;
      }

    }.init(index));
  }

  @Override
  public void onClick(View v) {
    if (v.getId() == mConfigureNow.getId()) {
      startOnboardingFor(0);

    } else if (v.getId() == mUseDefault.getId()) {
      mTimesContainer.removeAllViews();
      LayoutInflater inflater = LayoutInflater.from(this);
      inflater.inflate(R.layout.view_prayer_times, mTimesContainer);
      AppSettings.getInstance(this).set(AppSettings.Key.HAS_DEFAULT_SET, true);
      if (mLastLocation != null) {
        init();
      }
    }
  }

  private void startOnboardingFor(int index) {
    Intent intent = new Intent(getApplicationContext(), OnboardingActivity.class);
    intent.putExtra(OnboardingActivity.EXTRA_CARD_INDEX, index);
    startActivityForResult(intent, ONBOARDING_REQUEST);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_CHECK_SETTINGS) {
      switch (resultCode) {
        case Activity.RESULT_OK:
          // All required changes were successfully made
          fetchLocation();
          break;
        case Activity.RESULT_CANCELED:
          // The user was asked to change settings, but chose not to
          onLocationSettingsFailed();
          break;
        default:
          onLocationSettingsFailed();
          break;
      }
    } else if (requestCode == ONBOARDING_REQUEST) {
      if (resultCode == RESULT_OK) {
        mUseDefault.performClick();
      }
    } else {
      super.onActivityResult(requestCode, resultCode, data);
    }
  }

  /**
   * Callback received when a permissions request has been completed.
   */
  // UNCOMMENT WHEN SUPPORTING ANDROID-M STYLE RUNTIME PERMISSIONS
  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                         int[] grantResults) {

    if (requestCode == REQUEST_LOCATION) {
      if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        fetchLocation();
      } else {
        Log.i("BaseActivity", "LOCATION permission was NOT granted.");
        onLocationPermissionFailed();
      }

    } else {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
  }

  private void fetchLocation() {
    if (mLocationHelper != null) {
      mLocationHelper.checkLocationPermissions();
    }
  }

  @Override
  public void onLocationPermissionFailed() {

  }

  @Override
  public void onLocationSettingsFailed() {

  }

  @Override
  public void onLocationChanged(Location location) {
    mLastLocation = location;
    if (AppSettings.getInstance(this).getBoolean(AppSettings.Key.HAS_DEFAULT_SET)) {
      init();
    }
  }
}
