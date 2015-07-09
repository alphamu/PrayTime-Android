package com.alimuzaffar.ramadanalarm.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alimuzaffar.ramadanalarm.util.AppSettings;
import com.alimuzaffar.ramadanalarm.R;

public class InitialConfigFragment extends Fragment implements View.OnClickListener {

  OnOptionSelectedListener mCallback;
  TextView mConfigureNow;
  TextView mUseDefault;

  public static InitialConfigFragment newInstance() {
    InitialConfigFragment fragment = new InitialConfigFragment();
    return fragment;
  }

  public InitialConfigFragment() {
    // Required empty public constructor
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    if (activity instanceof OnOptionSelectedListener) {
      mCallback = (OnOptionSelectedListener) activity;
    } else {
      throw new IllegalArgumentException("activity should implement OnOptionSelectedListener");
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_initial_config, container, false);
    mConfigureNow = (TextView) view.findViewById(R.id.configure_now);
    mUseDefault = (TextView) view.findViewById(R.id.use_default);

    mConfigureNow.setOnClickListener(this);
    mUseDefault.setOnClickListener(this);
    return view;
  }


  @Override
  public void onClick(View v) {
    if (v.getId() == mConfigureNow.getId()) {
      mCallback.onConfigNowSelected(0);

    } else if (v.getId() == mUseDefault.getId()) {
      AppSettings.getInstance(getActivity()).set(AppSettings.Key.HAS_DEFAULT_SET, true);
      mCallback.onUseDefaultSelected();
    }
  }

  public interface OnOptionSelectedListener {
    void onConfigNowSelected(int num);
    void onUseDefaultSelected();
  }
}
