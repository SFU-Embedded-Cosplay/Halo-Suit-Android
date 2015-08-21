package com.haloproject.projectspartanv2.Fragments;

import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
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
public class CoolingFragment extends Fragment {
    private TempWheel waterTemp;
    private TextView flowPump;

    private RadioButton peltierAuto;
    private RadioButton peltierOff;

    private RadioButton headFansOn;
    private RadioButton headFansOff;

    private RadioButton waterPumpAuto;
    private RadioButton waterPumpOff;

    private TopBar mTopBar;
    private AndroidBlue mAndroidBlue;
    private DeviceHandlerCollection mDeviceHandlerCollection;

    private static final String ANDROID_BLUE_KEY = "androidBlue";
    private static final String DEVICE_HANDLER_COLLECTION_KEY = "deviceHandlerCollection";

    public static CoolingFragment newInstance(AndroidBlue mAndroidBlue, DeviceHandlerCollection mDeviceHandlerCollection) {
        CoolingFragment fragment = new CoolingFragment();

        final Bundle args = new Bundle();

        args.putSerializable(ANDROID_BLUE_KEY, mAndroidBlue);
        args.putSerializable(DEVICE_HANDLER_COLLECTION_KEY, mDeviceHandlerCollection);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mTopBar = MainActivity.mTopBar;

        mAndroidBlue = (AndroidBlue) getArguments().getSerializable(ANDROID_BLUE_KEY);
        mDeviceHandlerCollection = (DeviceHandlerCollection) getArguments().getSerializable(DEVICE_HANDLER_COLLECTION_KEY);


        mTopBar.setMenuName("Cooling");


        // load and store views from xml file
        View view = inflater.inflate(R.layout.fragment_cooling, container, false);
        waterTemp = (TempWheel) view.findViewById(R.id.waterTemp);
        flowPump = (TextView) view.findViewById(R.id.flowPump);

        peltierAuto = (RadioButton) view.findViewById(R.id.peltierauto);
        peltierOff = (RadioButton) view.findViewById(R.id.peltieroff);

        headFansOn = (RadioButton) view.findViewById(R.id.headfanson);
        headFansOff = (RadioButton) view.findViewById(R.id.headfansoff);

        waterPumpAuto = (RadioButton) view.findViewById(R.id.waterpumpauto);
        waterPumpOff = (RadioButton) view.findViewById(R.id.waterpumpoff);

        // set onclick listeners
        peltierAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeviceHandlerCollection.peltier.auto();
            }
        });
        peltierOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeviceHandlerCollection.peltier.off();
            }
        });

        headFansOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeviceHandlerCollection.headFans.on();
            }
        });
        headFansOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeviceHandlerCollection.headFans.off();
            }
        });

        waterPumpAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeviceHandlerCollection.waterPump.auto();
            }
        });
        waterPumpOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeviceHandlerCollection.waterPump.off();
            }
        });

        mAndroidBlue.setOnReceive(new Runnable() {
            @Override
            public void run() {
                waterTemp.setTemp(mDeviceHandlerCollection.waterTemperature.getValue());


                int flow = mDeviceHandlerCollection.flowRate.getValue();

                String newFlow = String.format("%d", flow);
                flowPump.setText(newFlow);

                peltierAuto.setChecked(mDeviceHandlerCollection.peltier.isAuto());
                peltierOff.setChecked(mDeviceHandlerCollection.peltier.isOff());

                headFansOn.setChecked(mDeviceHandlerCollection.headFans.isOn());
                headFansOff.setChecked(mDeviceHandlerCollection.headFans.isOff());

                waterPumpAuto.setChecked(mDeviceHandlerCollection.waterPump.isAuto());
                waterPumpOff.setChecked(mDeviceHandlerCollection.waterPump.isOff());
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