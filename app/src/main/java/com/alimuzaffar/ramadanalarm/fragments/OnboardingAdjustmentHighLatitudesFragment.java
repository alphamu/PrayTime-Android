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
 * Use the {@link OnboardingAdjustmentHighLatitudesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OnboardingAdjustmentHighLatitudesFragment extends OnboardingBaseFragment {
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  private static final String ARG_PARAM1 = "index_of_card";

  private int mParam1 = 0;

  private OnOnboardingOptionSelectedListener mListener;

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @param param1 Parameter 1.
   * @return A new instance of fragment OnboardingAdjustmentHighLatitudesFragment.
   */
  public static OnboardingAdjustmentHighLatitudesFragment newInstance(int param1) {
    OnboardingAdjustmentHighLatitudesFragment fragment = new OnboardingAdjustmentHighLatitudesFragment();
    Bundle args = new Bundle();
    args.putInt(ARG_PARAM1, param1);
    fragment.setArguments(args);
    return fragment;
  }

  public OnboardingAdjustmentHighLatitudesFragment() {
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
    return inflater.inflate(R.layout.fragment_onboarding_adjustment_high_latitudes, container, false);
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

  @Override
  public void onClick(View v) {

  }
}
