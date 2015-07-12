package com.alimuzaffar.ramadanalarm;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.alimuzaffar.ramadanalarm.util.AlarmUtils;
import com.alimuzaffar.ramadanalarm.util.ScreenUtils;

public class RingAlarmActivity extends AppCompatActivity implements Constants, View.OnClickListener {

  private Button mAlarmOff;
  private TextView mPrayerName;
  MediaPlayer mMediaPlayer = null;
  Runnable mAutoStop = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
        WindowManager.LayoutParams.FLAG_FULLSCREEN);

    ScreenUtils.lockOrientation(this);

    setContentView(R.layout.activity_ring_alarm);

    setVolumeControlStream(AudioManager.STREAM_ALARM);

    mPrayerName = (TextView) findViewById(R.id.prayer_name);
    mPrayerName.setText(getString(R.string.prayer_name_time, getIntent().getStringExtra(EXTRA_PRAYER_NAME)));

    mAlarmOff = (Button) findViewById(R.id.alarm_off);
    mAlarmOff.setOnClickListener(this);

    try {
      playAlarm();
    } catch (Exception e) {
      Log.e("RingAlarmActivity", e.getMessage(), e);
    }
  }

  private void playAlarm() throws Exception {
    Uri alert = AlarmUtils.getAlarmRingtoneUri();
    mMediaPlayer = new MediaPlayer();
    mMediaPlayer.setDataSource(this, alert);
    final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

    if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) == 0) {
      int volume = AlarmUtils.getAlarmVolumeFromPercentage(audioManager, 50f);
      audioManager.setStreamVolume(AudioManager.STREAM_ALARM, volume, 0);
    }

    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
    mMediaPlayer.setLooping(true);
    mMediaPlayer.prepare();
    mMediaPlayer.start();

    mAlarmOff.postDelayed(mAutoStop = new Runnable() {
      @Override
      public void run() {
        mAlarmOff.performClick();
      }
    }, FIVE_MINUTES);

  }

  private void stopAlarm() {
    if (mMediaPlayer != null) {
      mMediaPlayer.stop();
      mMediaPlayer.release();
      mMediaPlayer = null;
    }
    if (mAutoStop != null) {
      mAlarmOff.removeCallbacks(mAutoStop);
      mAutoStop = null;
    }
  }


  @Override
  public void onClick(View v) {
    stopAlarm();
    finish();
  }


  @Override
  public void onBackPressed() {
    //Do nothing since we want to force the user
    //to click the alarm button.
  }

  @Override
  protected void onDestroy() {
    stopAlarm();
    super.onDestroy();
  }

}
