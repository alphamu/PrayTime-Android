package com.alimuzaffar.ramadanalarm;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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

  GoogleApiClient mGoogleApiClient;
  Location mLastLocation;
  LocationRequest mLocationRequest;

  protected void getLocation() {
    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

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

  protected void initAppAfterCheckingLocation() {
    Log.d("SalaatTimesActivity", mLastLocation.getLatitude() + "," + mLastLocation.getLongitude());
    if (AppSettings.getInstance(this).getBoolean(AppSettings.Key.HAS_DEFAULT_SET)) {
      init();
    }
  }

  protected synchronized void buildGoogleApiClient() {
    mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(LocationServices.API)
        .build();

    mGoogleApiClient.connect();
  }

  protected LocationRequest createLocationRequest() {
    if (mLocationRequest == null) {
      mLocationRequest = new LocationRequest();
      mLocationRequest.setInterval(1000);
      mLocationRequest.setFastestInterval(500);
      mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    return mLocationRequest;
  }

  protected void checkIfLocationServicesEnabled() {
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
            getLocation();
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
            getLocation();
            break;
          case Activity.RESULT_CANCELED:
            // The user was asked to change settings, but chose not to
            break;
          default:
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

  protected abstract void init();
}
