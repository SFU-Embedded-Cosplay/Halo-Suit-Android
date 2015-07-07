package com.haloproject.projectspartanv2.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haloproject.bluetooth.AndroidBlue;
import com.haloproject.bluetooth.DeviceHandlerCollection;
import com.haloproject.projectspartanv2.MainActivity;
import com.haloproject.projectspartanv2.R;
import com.haloproject.projectspartanv2.view.TopBar;

/**
 * Created by Tyler on 6/29/2015.
 */
public class LightingFragment extends Fragment {
    private TopBar mTopBar;
    private DeviceHandlerCollection mDeviceHandlerCollection;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mTopBar = MainActivity.mTopBar;
        mDeviceHandlerCollection = DeviceHandlerCollection.getInstance();


//        mTopBar.setMenuName("Lighting");
        View view = inflater.inflate(R.layout.fragment_lighting, container, false);
        view.findViewById(R.id.mainlightson).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeviceHandlerCollection.mainLights.on();
            }
        });
        view.findViewById(R.id.mainlightsoff).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeviceHandlerCollection.mainLights.off();
            }
        });
        view.findViewById(R.id.mainlightsauto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeviceHandlerCollection.mainLights.auto();
            }
        });
        view.findViewById(R.id.redlightson).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeviceHandlerCollection.redHeadLight.on();
            }
        });
        view.findViewById(R.id.redlightsoff).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeviceHandlerCollection.redHeadLight.off();
            }
        });
        view.findViewById(R.id.whitelightson).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeviceHandlerCollection.whiteHeadLight.on();
            }
        });
        view.findViewById(R.id.whitelightsoff).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeviceHandlerCollection.whiteHeadLight.off();
            }
        });
        return view;
    }
}
