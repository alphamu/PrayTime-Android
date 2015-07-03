package com.alimuzaffar.ramadanalarm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.alimuzaffar.ramadanalarm.fragments.OnOnboardingOptionSelectedListener;
import com.alimuzaffar.ramadanalarm.fragments.OnboardingAdjustmentHighLatitudesFragment;
import com.alimuzaffar.ramadanalarm.fragments.OnboardingAsrCalculationMethodFragment;
import com.alimuzaffar.ramadanalarm.fragments.OnboardingCalculationMethodFragment;
import com.alimuzaffar.ramadanalarm.fragments.OnboardingTimeFormatFragment;

public class OnboardingActivity extends AppCompatActivity implements OnOnboardingOptionSelectedListener {

  public static final String EXTRA_CARD_INDEX = "card_index";

  private ViewPager mPager;
  private PagerAdapter mPagerAdapter;

  private int mCardIndex = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_onboarding);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    // Instantiate a ViewPager and a PagerAdapter.
    Intent intent = getIntent();
    mCardIndex = intent.getIntExtra(EXTRA_CARD_INDEX, 0);
    mPager = (ViewPager) findViewById(R.id.pager);
    mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), mCardIndex);
    mPager.setAdapter(mPagerAdapter);
  }


  @Override
  public void onBackPressed() {
    if (mPager.getCurrentItem() == 0) {
      // If the user is currently looking at the first step, allow the system to handle the
      // Back button. This calls finish() on this activity and pops the back stack.
      super.onBackPressed();
    } else {
      // Otherwise, select the previous step.
      mPager.setCurrentItem(mPager.getCurrentItem() - 1);
    }
  }

  @Override
  public void onOptionSelected() {
    if (mPager.getCurrentItem()+1 == mPagerAdapter.getCount()) {
     AppSettings.getInstance(this).set(AppSettings.Key.HAS_DEFAULT_SET, true);
      finish();
    } else {
      mPager.setCurrentItem(mPager.getCurrentItem() + 1);
    }
  }

  private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
    int mCardIndex = 0;

    public ScreenSlidePagerAdapter(FragmentManager fm, int cardIndex) {
      super(fm);
      mCardIndex = cardIndex;
    }

    @Override
    public Fragment getItem(int position) {
      switch (position) {
        case 0:
          return OnboardingCalculationMethodFragment.newInstance(mCardIndex);
        case 1:
          return OnboardingAsrCalculationMethodFragment.newInstance(mCardIndex);
        case 2:
          return OnboardingAdjustmentHighLatitudesFragment.newInstance(mCardIndex);
        case 3:
          return OnboardingTimeFormatFragment.newInstance(mCardIndex);
      }
      return null;
    }

    @Override
    public int getCount() {
      return 4;
    }
  }
}
