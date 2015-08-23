package com.alimuzaffar.ramadanalarm.fragments;


import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.alimuzaffar.ramadanalarm.BuildConfig;
import com.alimuzaffar.ramadanalarm.Constants;
import com.alimuzaffar.ramadanalarm.R;
import com.alimuzaffar.ramadanalarm.util.AppSettings;
import com.alimuzaffar.ramadanalarm.util.PermissionUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * A simple {@link Fragment} subclass.
 */
public class KaabaLocatorFragment extends Fragment implements Constants, OnMapReadyCallback, SensorEventListener {

  Location mLastLocation;
  MapFragment mMapFragment;
  GoogleMap mMap;

//  private float currentDegree = 0f;
  private SensorManager mSensorManager;

  private float[] mRotationMatrix = new float[16];
  private float[] mValues = new float[3];

  private boolean mRegistered = false;
  private static boolean sWriterExternalPermissionDenied;

  public KaabaLocatorFragment() {
    // Required empty public constructor
  }

  public static KaabaLocatorFragment newInstance(Location location) {
    KaabaLocatorFragment fragment = new KaabaLocatorFragment();
    Bundle args = new Bundle();
    args.putParcelable(EXTRA_LAST_LOCATION, location);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      mLastLocation = (Location) getArguments().getParcelable(EXTRA_LAST_LOCATION);
    }
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mSensorManager = (SensorManager) activity.getSystemService(Activity.SENSOR_SERVICE);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_kaaba_locator, container, false);
    view.findViewById(R.id.grant).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        checkPermissions();
      }
    });
    return view;
  }

  private boolean checkPermissions() {
    if (!PermissionUtil.hasSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
      if (!sWriterExternalPermissionDenied) {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL);
      } else {
        // Perhaps inform the user why they aren't seeing anything.
      }
      return false;
    }
    return true;
  }

  public void showMap() {
    if (!PermissionUtil.hasSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
      throw new IllegalStateException("WRITE_EXTERNAL_STORAGE permission should be granted before this method is called.");
    }

    if (mMapFragment == null) {
      mMapFragment = (MapFragment) getFragmentManager().findFragmentByTag("map_fragment");
    }

    if (mMapFragment == null) {
      GoogleMapOptions options = new GoogleMapOptions()
              .rotateGesturesEnabled(false)
              .tiltGesturesEnabled(false)
              .compassEnabled(true)
              .zoomControlsEnabled(false)
              .zoomGesturesEnabled(true)
              .scrollGesturesEnabled(true);
      mMapFragment = MapFragment.newInstance(options);
    }

    if (mMap == null && !mMapFragment.isAdded()) {
      ((ViewGroup) getView().findViewById(R.id.map_container)).removeAllViews();
      getFragmentManager().beginTransaction().add(R.id.map_container, mMapFragment, "map_fragment").commit();
      mMapFragment.getMapAsync(this);
      showOrientationDialog();
    } else {
      registerRotationListener();
    }
  }

  public void hideMap() {
    unregisterRotationListener();
  }

  @Override
  public void onMapReady(GoogleMap map) {
    mMap = map;
    if (mLastLocation != null) {
      initMap();
    }
  }

  private void initMap() {
    if (mMap == null && mLastLocation == null) {
      Log.w("KabaaLocatorFragment", "Ignoring since mMap or mLastLocation is null");
      return;
    }

    registerRotationListener();

    LatLng startPosition = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
    //21.4224698,39.8262066
    LatLng kaaba = new LatLng(21.4224698, 39.8262066);

    mMap.setMyLocationEnabled(true);
    mMap.getUiSettings().setMyLocationButtonEnabled(true);
    mMap.getUiSettings().setCompassEnabled(false);
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPosition, 13));
    //mMap.getUiSettings().setRotateGesturesEnabled(false);

    mMap.clear();

    mMap.addMarker(new MarkerOptions()
        .title(getString(R.string.kaaba))
        .position(kaaba));

    mMap.addMarker(new MarkerOptions()
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.compass))
        .title(getString(R.string.current_location))
        .position(startPosition));

    // Polylines are useful for marking paths and routes on the map.
    mMap.addPolyline(new PolylineOptions().geodesic(true)
        .add(startPosition)  // user position
        .add(kaaba)
        .color(Color.RED));  // Kaabah

  }

  public void setLocation(Location location) {
    mLastLocation = location;
    initMap();
  }

  @Override
  public void onResume() {
    super.onResume();
    registerRotationListener();
  }

  @Override
  public void onPause() {
    super.onPause();
    unregisterRotationListener();
  }

  private void registerRotationListener() {
    if (mMap != null && mLastLocation != null && !mRegistered) {
      mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_UI);
      mRegistered = true;
    }
  }

  private void unregisterRotationListener() {
    if (mRegistered) {
      mSensorManager.unregisterListener(this);
      mRegistered = false;
    }
  }

  @Override
  public void onSensorChanged(SensorEvent event) {
    if (mLastLocation == null) {
      return;
    }
    SensorManager.getRotationMatrixFromVector(mRotationMatrix, event.values);
    SensorManager.getOrientation(mRotationMatrix, mValues);

    /*
    if (BuildConfig.DEBUG) {
      System.out.println("sensorChanged (" + Math.toDegrees(mValues[0]) + ", " + Math.toDegrees(mValues[1]) + ", " + Math.toDegrees(mValues[2]) + ")");
    }
    */

    float bearing = 0f;

    if (mMap != null) {
      bearing = Double.valueOf(mMap.getCameraPosition().bearing).floatValue();
    }

    // get the angle around the z-axis rotated
    float degree = Double.valueOf(Math.toDegrees(mValues[0])).floatValue();

    if (Math.round(bearing) == Math.round(degree)) {
      System.out.println("bearing and degrees are the same.");
      return;
    }

    if (BuildConfig.DEBUG) {
      System.out.println("degrees " + degree + ", bearing " + bearing);
    }

    //tvHeading.setText("Heading: " + Float.toString(degree) + " degrees");

    CameraPosition cameraPosition = mMap.getCameraPosition();
    CameraPosition newPosition = new CameraPosition(cameraPosition.target, cameraPosition.zoom, cameraPosition.tilt, degree);
    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(newPosition));
    // create a rotation animation (reverse turn degree degrees)
    /*
    RotateAnimation ra = new RotateAnimation(
            currentDegree,
            -degree-180,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f);

    // how long the animation will take place
    ra.setDuration(210);

    // set the animation after the end of the reservation status
    ra.setFillAfter(true);

    // Start the animation
    mCompass.startAnimation(ra);
    currentDegree = -degree-180;
    */
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {

  }

  /**
   * Callback received when a permissions request has been completed.
   */
  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                         int[] grantResults) {

    if (requestCode == REQUEST_WRITE_EXTERNAL) {
      if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
       showMap();
      } else {
        Log.i("BaseActivity", "LOCATION permission was NOT granted.");
        sWriterExternalPermissionDenied = true;
      }

    } else {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
  }

  private void showOrientationDialog() {
    if (!AppSettings.getInstance().getBoolean(AppSettings.Key.SHOW_ORIENATATION_INSTRACTIONS, true)) {
      return;
    }
    View v = getActivity().getLayoutInflater().inflate(R.layout.view_orientation_instructions, null);
    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
    alertBuilder.setView(v)
        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        });

    CheckBox doNotShow = (CheckBox) v.findViewById(R.id.checkbox_no_show);
    doNotShow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        AppSettings.getInstance().set(AppSettings.Key.SHOW_ORIENATATION_INSTRACTIONS, !isChecked);
      }
    });

    alertBuilder.create().show();
  }

}
