package com.haloproject.projectspartanv2.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haloproject.bluetooth.AndroidBlue;
import com.haloproject.bluetooth.DeviceHandlerCollection;
import com.haloproject.projectspartanv2.MainActivity;
import com.haloproject.projectspartanv2.R;
import com.haloproject.projectspartanv2.view.TempWheel;
import com.haloproject.projectspartanv2.view.TopBar;

/**
 * Created by Tyler on 6/29/2015.
 */
public class VitalsFragment extends Fragment {
    private TempWheel headTemp;
    private TempWheel armpitsTemp;
    private TempWheel crotchTemp;
    private TextView heartRate;

    private TopBar mTopBar;
    private AndroidBlue mAndroidBlue;
    private DeviceHandlerCollection mDeviceHandlerCollection;

    private static final String ANDROID_BLUE_KEY = "androidBlue";
    private static final String DEVICE_HANDLER_COLLECTION_KEY = "deviceHandlerCollection";

    public static VitalsFragment newInstance(AndroidBlue mAndroidBlue, DeviceHandlerCollection mDeviceHandlerCollection) {
        VitalsFragment fragment = new VitalsFragment();

        final Bundle args = new Bundle();

        args.putSerializable(ANDROID_BLUE_KEY, mAndroidBlue);
        args.putSerializable(DEVICE_HANDLER_COLLECTION_KEY, mDeviceHandlerCollection);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mTopBar = MainActivity.mTopBar;

        mAndroidBlue = (AndroidBlue) getArguments().getSerializable(ANDROID_BLUE_KEY);
        mDeviceHandlerCollection = (DeviceHandlerCollection) getArguments().getSerializable(DEVICE_HANDLER_COLLECTION_KEY);


        mTopBar.setMenuName("Vitals");
        View view = inflater.inflate(R.layout.fragment_vitals, container, false);
        headTemp = (TempWheel) view.findViewById(R.id.headTemp);
        armpitsTemp = (TempWheel) view.findViewById(R.id.armpitsTemp);
        crotchTemp = (TempWheel) view.findViewById(R.id.crotchTemp);
        heartRate = (TextView) view.findViewById(R.id.heartRate);

        mAndroidBlue.setOnReceive(new Runnable() {
            @Override
            public void run() {
                headTemp.setTemp(mDeviceHandlerCollection.headTemperature.getValue());
                armpitsTemp.setTemp(mDeviceHandlerCollection.armpitsTemperature.getValue());
                crotchTemp.setTemp(mDeviceHandlerCollection.crotchTemperature.getValue());

                int hr = mDeviceHandlerCollection.heartRate.getValue();
                String heartrate = String.format("%d", hr);
                heartRate.setText(heartrate);
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAndroidBlue.changeOnReceive();
    }
}
