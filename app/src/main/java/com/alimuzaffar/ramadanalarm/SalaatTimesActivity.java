package com.alimuzaffar.ramadanalarm;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.LinkedHashMap;
import java.util.TimeZone;


public class SalaatTimesActivity extends AppCompatActivity {

  public static final String EXTRA_ALARM_INDEX = "alarm_index";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_salaat_times);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    if (!AppSettings.getInstance(this).getBoolean(AppSettings.Key.HAS_DEFAULT_SET)) {
      Intent intent = new Intent(this, OnboardingActivity.class);
      intent.putExtra(OnboardingActivity.EXTRA_CARD_INDEX, 0);
      startActivity(intent);

    }


    // In future releases we will add more cards.
    // Then we'll need to do this for each card.
    // For now it's included in the layout which
    // makes it easier to work with the layout editor.
    // ViewGroup timesContainer = (ViewGroup) findViewById(R.id.times_container);
    // LayoutInflater inflater = LayoutInflater.from(this);
    // inflater.inflate(R.layout.prayer_times, timesContainer, true);

    //Toolbar will now take on default Action Bar characteristics
    LinkedHashMap<String, String> prayerTimes = PrayTime.getPrayerTimes(this);

    TextView title = findView(R.id.card_title);
    title.setText(TimeZone.getDefault().getID());

    TextView fajr = findView(R.id.fajr);
    TextView dhuhr = findView(R.id.dhuhr);
    TextView asr = findView(R.id.asr);
    TextView maghrib = findView(R.id.maghrib);
    TextView isha = findView(R.id.isha);
    TextView sunrise = findView(R.id.sunrise);
    TextView sunset = findView(R.id.sunset);
    TextView alarm = findView(R.id.alarm);

    fajr.setText(prayerTimes.get(fajr.getTag()));
    dhuhr.setText(prayerTimes.get(dhuhr.getTag()));
    asr.setText(prayerTimes.get(asr.getTag()));
    maghrib.setText(prayerTimes.get(maghrib.getTag()));
    isha.setText(prayerTimes.get(isha.getTag()));
    sunrise.setText(prayerTimes.get(sunrise.getTag()));
    sunset.setText(prayerTimes.get(sunset.getTag()));

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
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  <T extends View> T findView(@IdRes int resId) {
    return (T) findViewById(resId);
  }

  private void setAlarmButtonText(TextView button, int index) {
    boolean isAlarmSet = AppSettings.getInstance(this).isAlarmSetFor(index);
    int isAlarmSetInt = isAlarmSet? 0 : 1;
    String buttonText = getResources().getQuantityString(R.plurals.button_alarm, isAlarmSetInt);
    button.setText(buttonText);
  }

  private void setAlarmButtonClickListener(TextView alarm, int index) {
    alarm.setOnClickListener(new View.OnClickListener(){
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

}
