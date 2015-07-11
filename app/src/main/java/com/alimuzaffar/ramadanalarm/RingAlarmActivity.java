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
    Uri alert = getAlarmRingtoneUri();
    mMediaPlayer = new MediaPlayer();
    mMediaPlayer.setDataSource(this, alert);
    final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

    if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) == 0) {
      int volume = getAlarmVolumeFromPercentage(audioManager, 50f);
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
    }
    if (mAutoStop != null) {
      mAlarmOff.removeCallbacks(mAutoStop);
    }

    finish();
  }

  private Uri getAlarmRingtoneUri() {
    Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
    if (alert == null) {
      // alert is null, using backup
      alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
      if (alert == null) { // I can't see this ever being null (as always
        // have a default notification) but just incase
        // alert backup is null, using 2nd backup
        alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
      }
    }
    return alert;
  }

  public static int getAlarmVolumeFromPercentage(AudioManager audioManager, float percentage) {
    int volume = (int) Math.ceil((double) audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM) * (percentage / 100.0d));
    return volume;
  }

  @Override
  public void onClick(View v) {
    stopAlarm();
  }
}
