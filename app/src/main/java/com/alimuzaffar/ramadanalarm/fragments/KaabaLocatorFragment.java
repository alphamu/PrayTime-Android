package com.alimuzaffar.ramadanalarm.fragments;


import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.alimuzaffar.ramadanalarm.BuildConfig;
import com.alimuzaffar.ramadanalarm.Constants;
import com.alimuzaffar.ramadanalarm.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * A simple {@link Fragment} subclass.
 */
public class KaabaLocatorFragment extends Fragment implements Constants, OnMapReadyCallback, SensorEventListener {

  Location mLastLocation;
  SupportMapFragment mMapFragment;
  GoogleMap mMap;

  private float currentDegree = 0f;
  private SensorManager mSensorManager;

  private float[] mRotationMatrix = new float[16];
  private float[] mValues = new float[3];

  private ImageView mCompass;

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
    mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
    mMapFragment.getMapAsync(this);
    mCompass = (ImageView) view.findViewById(R.id.compass);

    return view;
  }

  @Override
  public void onMapReady(GoogleMap map) {
    mMap = map;
    if (mLastLocation != null) {
      initMap();
    }
  }

  public void initMap() {
    if (mMap == null && mLastLocation == null) {
      Log.w("KabaaLocatorFragment", "Ignoring since mMap or mLastLocation is null");
      return;
    }

    LatLng startPosition = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
    //21.4224698,39.8262066
    LatLng kaaba = new LatLng(21.4224698, 39.8262066);

    mMap.setMyLocationEnabled(true);
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPosition, 13));
    mMap.getUiSettings().setCompassEnabled(true);
    mMap.getUiSettings().setRotateGesturesEnabled(false);

    mMap.addMarker(new MarkerOptions()
            .title(getString(R.string.kaaba))
            .position(kaaba));

    mMap.addMarker(new MarkerOptions()
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
    mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_FASTEST);
  }

  @Override
  public void onPause() {
    super.onPause();
    mSensorManager.unregisterListener(this);
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

    float bearing = 0;

    if (mMap != null) {
      bearing = Double.valueOf(mMap.getCameraPosition().bearing).floatValue();
      System.out.println("Bearing = "+bearing);
    }


    // get the angle around the z-axis rotated
    float degree = Double.valueOf(Math.toDegrees(mValues[0])).floatValue();
    if (BuildConfig.DEBUG) {
      System.out.println("degrees " + -degree);
    }


    //tvHeading.setText("Heading: " + Float.toString(degree) + " degrees");

    if (mCompass == null) {
      return;
    }

    // create a rotation animation (reverse turn degree degrees)
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
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {

  }
}
