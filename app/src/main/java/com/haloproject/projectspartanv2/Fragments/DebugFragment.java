package com.haloproject.projectspartanv2.Fragments;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haloproject.bluetooth.AndroidBlue;
import com.haloproject.bluetooth.DeviceHandlerCollection;
import com.haloproject.projectspartanv2.MainActivity;
import com.haloproject.projectspartanv2.R;
import com.haloproject.projectspartanv2.view.BatteryBar;
import com.haloproject.projectspartanv2.view.TopBar;

import org.json.JSONException;

import java.util.Calendar;

/**
 * Created by AlexLand on 2016-01-12.
 */
public class DebugFragment extends Fragment{
    private TopBar mTopBar;
    private AndroidBlue mAndroidBlue;
    private DeviceHandlerCollection mDeviceHandlerCollection;

    private static final String ANDROID_BLUE_KEY = "androidBlue";
    private static final String DEVICE_HANDLER_COLLECTION_KEY = "deviceHandlerCollection";

    public static DebugFragment newInstance(AndroidBlue mAndroidBlue, DeviceHandlerCollection mDeviceHandlerCollection) {
        DebugFragment fragment = new DebugFragment();

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

        mTopBar.setMenuName("Debug");

        View view = inflater.inflate(R.layout.fragment_debug, container, false);

        final TextView debugText = (TextView) view.findViewById(R.id.debugText);

        mAndroidBlue.setOnReceive(new Runnable() {
            @Override
            public void run() {
                String json = "";
                try {
                    json = mAndroidBlue.getJSON().toString(4);
                } catch (JSONException e) {
                    json = mAndroidBlue.getJSON().toString();
                }
                debugText.setText(json);
            }
        });
        return view;
    }

}
