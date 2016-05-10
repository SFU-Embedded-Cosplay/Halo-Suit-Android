package com.haloproject.projectspartanv2.Fragments;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haloproject.bluetooth.AndroidBlue;
import com.haloproject.bluetooth.DeviceHandlerCollection;
import com.haloproject.projectspartanv2.MainActivity;
import com.haloproject.projectspartanv2.R;
import com.haloproject.projectspartanv2.view.BatteryBar;
import com.haloproject.projectspartanv2.view.TopBar;

/**
 * Created by Tyler on 6/29/2015.
 */
public class BatteryFragment extends Fragment {
    private BatteryBar highAmp;
    private BatteryBar lowAmp;
    private BatteryBar android;
    private BatteryBar glass;

    private TopBar mTopBar;
    private AndroidBlue mAndroidBlue;
    private DeviceHandlerCollection mDeviceHandlerCollection;

    private static final String ANDROID_BLUE_KEY = "androidBlue";
    private static final String DEVICE_HANDLER_COLLECTION_KEY = "deviceHandlerCollection";

    public static BatteryFragment newInstance(AndroidBlue mAndroidBlue, DeviceHandlerCollection mDeviceHandlerCollection) {
        BatteryFragment fragment = new BatteryFragment();

        final Bundle args = new Bundle();

        args.putSerializable(ANDROID_BLUE_KEY, mAndroidBlue);
        args.putSerializable(DEVICE_HANDLER_COLLECTION_KEY, mDeviceHandlerCollection);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mTopBar = MainActivity.mTopBar;

        mAndroidBlue = (AndroidBlue) getArguments().getSerializable(ANDROID_BLUE_KEY);
        mDeviceHandlerCollection = (DeviceHandlerCollection) getArguments().getSerializable(DEVICE_HANDLER_COLLECTION_KEY);


        mTopBar.setMenuName("Batteries");

        View view = inflater.inflate(R.layout.fragment_battery, container, false);

        highAmp = (BatteryBar) view.findViewById(R.id.highampbattery);
        lowAmp = (BatteryBar) view.findViewById(R.id.lowampbattery);
        android = (BatteryBar) view.findViewById(R.id.androidbattery);
        glass = (BatteryBar) view.findViewById(R.id.glassbattery);

        android.setBatteryCharge(getBatteryLevel());

        mAndroidBlue.setOnReceive(new Runnable() {
            @Override
            public void run() {
                android.setBatteryCharge(getBatteryLevel());
                highAmp.setBatteryCharge(mDeviceHandlerCollection.battery8AH.getValue());
                lowAmp.setBatteryCharge(mDeviceHandlerCollection.battery2AH.getValue());
                glass.setBatteryCharge(mDeviceHandlerCollection.batteryGlass.getValue());
            }
        });
        return view;
    }

    private int getBatteryLevel() {
        Intent batteryIntent = this.getActivity().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        if(level == -1 || scale == -1) {
            return 50;
        }

        Float batteryLevel = ((float)level / (float)scale) * 100.0f;
        return batteryLevel.intValue();
    }
}
