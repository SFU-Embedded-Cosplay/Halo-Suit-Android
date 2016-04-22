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
    private AndroidBlue mAndroidBlue;

    private static final String ANDROID_BLUE_KEY = "androidBlue";
    private static final String DEVICE_HANDLER_COLLECTION_KEY = "deviceHandlerCollection";

    public static LightingFragment newInstance(AndroidBlue mAndroidBlue, DeviceHandlerCollection mDeviceHandlerCollection) {
        LightingFragment fragment = new LightingFragment();

        final Bundle args = new Bundle();

        args.putSerializable(DEVICE_HANDLER_COLLECTION_KEY, mDeviceHandlerCollection);
        args.putSerializable(ANDROID_BLUE_KEY, mAndroidBlue);

        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mTopBar = MainActivity.mTopBar;
        mDeviceHandlerCollection = (DeviceHandlerCollection) getArguments().getSerializable(DEVICE_HANDLER_COLLECTION_KEY);
        mAndroidBlue = (AndroidBlue) getArguments().getSerializable(ANDROID_BLUE_KEY);


        mTopBar.setMenuName("Lighting");
        View view = inflater.inflate(R.layout.fragment_lighting, container, false);

        View mainLightsOn = view.findViewById(R.id.mainlightson);
        View mainLightsOff = view.findViewById(R.id.mainlightsoff);
        View mainLightsAuto = view.findViewById(R.id.mainlightsauto);

        View redLightsOn = view.findViewById(R.id.redlightson);
        View redLightsOff = view.findViewById(R.id.redlightsoff);

        View whiteLightsOn = view.findViewById(R.id.whitelightson);
        View whiteLightsOff = view.findViewById(R.id.whitelightsoff);

        mainLightsOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeviceHandlerCollection.mainLights.on();
            }
        });
        mainLightsOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeviceHandlerCollection.mainLights.off();
            }
        });
        mainLightsAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeviceHandlerCollection.mainLights.auto();
            }
        });

        redLightsOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeviceHandlerCollection.redHeadLight.on();
            }
        });
        redLightsOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeviceHandlerCollection.redHeadLight.off();
            }
        });

        whiteLightsOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeviceHandlerCollection.whiteHeadLight.on();
            }
        });
        whiteLightsOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeviceHandlerCollection.whiteHeadLight.off();
            }
        });

        return view;
    }
}
