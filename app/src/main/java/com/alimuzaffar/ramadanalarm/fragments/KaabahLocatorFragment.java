package com.alimuzaffar.ramadanalarm.fragments;


import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alimuzaffar.ramadanalarm.Constants;
import com.alimuzaffar.ramadanalarm.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class KaabahLocatorFragment extends Fragment implements Constants {

  Location mLastLocation;

  public KaabahLocatorFragment() {
    // Required empty public constructor
  }

  public static KaabahLocatorFragment newInstance(Location location) {
    KaabahLocatorFragment fragment = new KaabahLocatorFragment();
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
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_kaabah_locator, container, false);
  }


}
