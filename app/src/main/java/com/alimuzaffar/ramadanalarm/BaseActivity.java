package com.alimuzaffar.ramadanalarm;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;


public abstract class BaseActivity extends AppCompatActivity implements
    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


  public final int REQUEST_CHECK_SETTINGS = 101;
  public final int REQUEST_LOCATION = 103;

  GoogleApiClient mGoogleApiClient;
  Location mLastLocation;
  LocationRequest mCoarseLocationRequest;

  protected void checkLocationPermissions() {
    if (PermissionUtil.hasSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
      initAppAfterCheckingLocation();
    } else {
      // UNCOMMENT TO SUPPORT ANDROID M RUNTIME PERMISSIONS
      //requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
    }
  }

  private void initAppAfterCheckingLocation() {
    if (mGoogleApiClient == null) {
      buildGoogleApiClient();
    } else if (mLastLocation == null && mGoogleApiClient.isConnected()) {
      // check for a location.
      checkIfLocationServicesEnabled();
    } else if (mLastLocation == null && mGoogleApiClient.isConnecting()) {
      // need to wait, this method will be called again after onConnect.
    } else {
      Log.d("SalaatTimesActivity", mLastLocation.getLatitude() + "," + mLastLocation.getLongitude());
      if (AppSettings.getInstance(this).getBoolean(AppSettings.Key.HAS_DEFAULT_SET)) {
        init();
      }
    }
  }

  private synchronized void buildGoogleApiClient() {
    mGoogleApiClient = new GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build();

    mGoogleApiClient.connect();
  }

  private void checkLocationAndInit() {
    if (mLastLocation == null) {
      mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    if (mLastLocation == null) {
      LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, createLocationRequest(), new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
          mLastLocation = location;
          initAppAfterCheckingLocation();
          LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
      });
    } else {
      initAppAfterCheckingLocation();
    }
  }

  private LocationRequest createLocationRequest() {
    if (mCoarseLocationRequest == null) {
      mCoarseLocationRequest = new LocationRequest();
      mCoarseLocationRequest.setInterval(10000);
      mCoarseLocationRequest.setFastestInterval(1000);
      mCoarseLocationRequest.setNumUpdates(1);
      mCoarseLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }
    return mCoarseLocationRequest;
  }

  private void checkIfLocationServicesEnabled() {
    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
        .addLocationRequest(createLocationRequest());

    PendingResult<LocationSettingsResult> result =
        LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
      @Override
      public void onResult(LocationSettingsResult result) {
        final Status status = result.getStatus();
        switch (status.getStatusCode()) {
          case LocationSettingsStatusCodes.SUCCESS:
            // All location settings are satisfied. The client can initialize location
            // requests here.
            checkLocationAndInit();
            break;
          case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
            // Location settings are not satisfied. But could be fixed by showing the user
            // a dialog.
            try {
              // Show the dialog by calling startResolutionForResult(),
              // and check the result in onActivityResult().
              status.startResolutionForResult(BaseActivity.this, REQUEST_CHECK_SETTINGS);
            } catch (IntentSender.SendIntentException e) {
              // Ignore the error.
            }
            break;
          case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
            // Location settings are not satisfied. However, we have no way to fix the
            // settings so we won't show the dialog.
            break;
        }
      }
    });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
    switch (requestCode) {
      case REQUEST_CHECK_SETTINGS:
        switch (resultCode) {
          case Activity.RESULT_OK:
            // All required changes were successfully made
            checkLocationAndInit();
            break;
          case Activity.RESULT_CANCELED:
            // The user was asked to change settings, but chose not to
            locationSettingsFailed();
            break;
          default:
            locationSettingsFailed();
            break;
        }
        break;
    }
  }

  @Override
  public void onConnected(Bundle bundle) {
    checkIfLocationServicesEnabled();
  }

  @Override
  public void onConnectionSuspended(int i) {

  }

  @Override
  public void onConnectionFailed(ConnectionResult connectionResult) {

  }

  /**
   * Callback received when a permissions request has been completed.
   */
/*
  // UNCOMMENT WHEN SUPPORTING ANDROID-M STYLE RUNTIME PERMISSIONS
  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                         int[] grantResults) {

    if (requestCode == REQUEST_LOCATION) {
      if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        checkLocationPermissions();
      } else {
        Log.i("BaseActivity", "LOCATION permission was NOT granted.");
        locationPermissionFailed();
      }

    } else {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
  }
*/

  protected abstract void init();

  protected abstract void locationPermissionFailed();

  protected abstract void locationSettingsFailed();
}
