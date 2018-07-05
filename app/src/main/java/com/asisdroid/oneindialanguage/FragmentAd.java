package com.asisdroid.oneindialanguage;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;


public class FragmentAd extends Fragment {

	private AdView mAdView;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		    View view = inflater.inflate(R.layout.fragment_ad,
		        container, false);
			MobileAds.initialize(getActivity(), getResources().getString(R.string.admobappID));
			mAdView = (AdView) view.findViewById(R.id.adView);
			AdRequest adRequest = new AdRequest.Builder().build();
			mAdView.loadAd(adRequest);
		    return view;
		  
}
}