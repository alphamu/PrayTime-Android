package com.alimuzaffar.ramadanalarm.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alimuzaffar.ramadanalarm.AppSettings;
import com.alimuzaffar.ramadanalarm.utils.PrayTime;
import com.alimuzaffar.ramadanalarm.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnOnboardingOptionSelectedListener} interface
 * to handle interaction events.
 * Use the {@link OnboardingTimeFormatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OnboardingTimeFormatFragment extends OnboardingBaseFragment {
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  private static final String ARG_PARAM1 = "param1";

  private int mParam1 = 0;

  protected OnOnboardingOptionSelectedListener mListener;

  TextView m12h;
  TextView m24h;

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @param param1 Parameter 1.
   * @return A new instance of fragment OnboardingAsrCalculationMethod.
   */
  public static OnboardingTimeFormatFragment newInstance(int param1) {
    OnboardingTimeFormatFragment fragment = new OnboardingTimeFormatFragment();
    Bundle args = new Bundle();
    args.putInt(ARG_PARAM1, param1);
    fragment.setArguments(args);
    return fragment;
  }

  public OnboardingTimeFormatFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      mParam1 = getArguments().getInt(ARG_PARAM1);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_onboarding_time_format, container, false);

    view.findViewById(R.id.prev).setOnClickListener(this);
    TextView next = (TextView) view.findViewById(R.id.next);
    next.setOnClickListener(this);
    next.setText(R.string.button_done);

    TextView title = (TextView) view.findViewById(R.id.card_title);
    title.setText(R.string.time_title);

    m12h = (TextView) view.findViewById(R.id.twelve);
    m24h = (TextView) view.findViewById(R.id.twenty_four);
    m12h.setOnClickListener(this);
    m24h.setOnClickListener(this);

    int method = AppSettings.getInstance(getActivity()).getTimeFormatFor(mParam1);
    if (method == PrayTime.TIME_12) {
      m12h.setSelected(true);
    } else {
      m24h.setSelected(true);
    }

    return view;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      mListener = (OnOnboardingOptionSelectedListener) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(activity.toString()
              + " must implement OnOnboardingOptionSelectedListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  @Override
  public void onClick(View v) {
    AppSettings settings = AppSettings.getInstance(getActivity());
    if (v.getId() == R.id.next) {
      mListener.onOptionSelected();
    } else if (v.getId() == R.id.prev) {
      getActivity().onBackPressed();
    } else if (v.getId() == m12h.getId()) {
      m12h.setSelected(true);
      m24h.setSelected(false);
      settings.setTimeFormatFor(mParam1, PrayTime.TIME_12);
      mListener.onOptionSelected();
    } else if (v.getId() == m24h.getId()) {
      m12h.setSelected(false);
      m24h.setSelected(true);
      settings.setTimeFormatFor(mParam1, PrayTime.TIME_24);
      mListener.onOptionSelected();
    }
  }
}
