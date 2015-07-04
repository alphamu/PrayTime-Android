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

import com.alimuzaffar.ramadanalarm.fragments.InitialConfigFragment;
import com.alimuzaffar.ramadanalarm.fragments.LocationHelper;
import com.alimuzaffar.ramadanalarm.fragments.SalaatTimesFragment;
import com.alimuzaffar.ramadanalarm.utils.PrayTime;

import java.util.LinkedHashMap;
import java.util.TimeZone;


public class SalaatTimesActivity extends BaseActivity implements Constants, View.OnClickListener, InitialConfigFragment.OnOptionSelectedListener {

  ViewGroup mTimesContainer;
  TextView mUseDefault;

  private LocationHelper mLocationHelper;
  private SalaatTimesFragment mTimesFragment;
  private InitialConfigFragment mConfigFragment;
  private Location mLastLocation = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_salaat_times);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    mTimesContainer = (ViewGroup) findViewById(R.id.times_container);
    mLocationHelper = (LocationHelper) getSupportFragmentManager().findFragmentByTag(LOCATION_FRAGMENT);

    if (!AppSettings.getInstance(this).getBoolean(AppSettings.Key.HAS_DEFAULT_SET)) {
      mConfigFragment = (InitialConfigFragment) getSupportFragmentManager().findFragmentByTag(CONFIG_FRAGMENT);
      if (mConfigFragment == null) {
        mConfigFragment = InitialConfigFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mConfigFragment, CONFIG_FRAGMENT).commit();
      }
    }

    if(mLocationHelper == null) {
      mLocationHelper = LocationHelper.newInstance();
      getSupportFragmentManager().beginTransaction().add(mLocationHelper, LOCATION_FRAGMENT).commit();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();

    if (mLastLocation == null) {
      fetchLocation();
    }
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

  @Override
  public void onClick(View v) {

  }

  private void initTimes() {
    if (AppSettings.getInstance(this).getBoolean(AppSettings.Key.HAS_DEFAULT_SET)) {
      mTimesFragment = (SalaatTimesFragment) getSupportFragmentManager().findFragmentByTag(TIMES_FRAGMENT);

      if (mTimesFragment == null) {
        mTimesFragment = SalaatTimesFragment.newInstance(0, mLastLocation);
      } else {
        mTimesFragment.setLocation(mLastLocation);
      }

      if (!mTimesFragment.isAdded()) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mTimesFragment, TIMES_FRAGMENT).commit();
      }
    }
  }

  private void startOnboardingFor(int index) {
    Intent intent = new Intent(getApplicationContext(), OnboardingActivity.class);
    intent.putExtra(OnboardingActivity.EXTRA_CARD_INDEX, index);
    startActivityForResult(intent, REQUEST_ONBOARDING);
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
    } else if (requestCode == REQUEST_ONBOARDING) {
      if (resultCode == RESULT_OK) {
        onUseDefaultSelected();
      }
    } else {
      super.onActivityResult(requestCode, resultCode, data);
    }
  }

  /**
   * Callback received when a permissions request has been completed.
   */
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
    initTimes();
  }

  @Override
  public void onConfigNowSelected(int num) {
    startOnboardingFor(num);
  }

  @Override
  public void onUseDefaultSelected() {
    if (mLastLocation != null) {
      initTimes();
    }
  }
}
