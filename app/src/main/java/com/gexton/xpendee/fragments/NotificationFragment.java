package com.gexton.xpendee.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gexton.xpendee.R;
import com.gexton.xpendee.util.AdUtil;
import com.google.android.gms.ads.AdView;

public class NotificationFragment extends Fragment {
    View view;
    AdView adView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_notification, container, false);

        adView = view.findViewById(R.id.adView);
        AdUtil adUtil = new AdUtil(getActivity());
        adUtil.loadBannerAd(adView);

        return view;
    }
}