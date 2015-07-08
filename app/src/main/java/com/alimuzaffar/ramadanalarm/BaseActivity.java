package com.alimuzaffar.ramadanalarm;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.alimuzaffar.ramadanalarm.fragments.LocationHelper;


public abstract class BaseActivity extends AppCompatActivity implements Constants, LocationHelper.LocationCallback {

  //UNCOMMENT THE @Override AND THE super CALLS WHEN MNC ALLOWS BUILDING
  //AGAINST OLDER BUILDS OR M SDK IS LAUNCHED IN WHICH CASE WE CAN REMOVE BaseActivity

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  //@Override
  public int checkSelfPermission(String permission) {
    return super.checkSelfPermission(permission);
//    return 0;
  }

  public final void requestPermissionsProxy(@NonNull String[] permissions, int requestCode) {
    requestPermissions(permissions, requestCode);
  }
}
