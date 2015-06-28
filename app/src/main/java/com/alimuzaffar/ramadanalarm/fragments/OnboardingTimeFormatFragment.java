package com.alimuzaffar.ramadanalarm.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

  private int mParam1;

  private OnOnboardingOptionSelectedListener mListener;

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @param param1 Parameter 1.
   * @return A new instance of fragment OnboardingTimeFormatFragment.
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
    // should be the last, dont need this.
    view.findViewById(R.id.next).setVisibility(View.INVISIBLE);
    return view;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      mListener = (OnOnboardingOptionSelectedListener) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(activity.toString()
          + " must implement OnFragmentInteractionListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }
}
