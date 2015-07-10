package com.haloproject.projectspartanv2.Fragments;

import android.bluetooth.BluetoothClass;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mTopBar = MainActivity.mTopBar;
        mAndroidBlue = AndroidBlue.getInstance();
        mDeviceHandlerCollection = DeviceHandlerCollection.getInstance();


        mTopBar.setMenuName("Batteries");

        View view = inflater.inflate(R.layout.fragment_battery, container, false);

        highAmp = (BatteryBar) view.findViewById(R.id.highampbattery);
        lowAmp = (BatteryBar) view.findViewById(R.id.lowampbattery);
        android = (BatteryBar) view.findViewById(R.id.androidbattery);
        glass = (BatteryBar) view.findViewById(R.id.glassbattery);

        mAndroidBlue.setOnReceive(new Runnable() {
            @Override
            public void run() {
                highAmp.setBatteryCharge(mDeviceHandlerCollection.battery8AH.getValue());
                lowAmp.setBatteryCharge(mDeviceHandlerCollection.battery2AH.getValue());
                android.setBatteryCharge(mDeviceHandlerCollection.batteryAndroid.getValue());
                glass.setBatteryCharge(mDeviceHandlerCollection.batteryGlass.getValue());
            }
        });
        return view;
    }
}
