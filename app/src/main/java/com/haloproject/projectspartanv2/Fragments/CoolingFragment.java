package com.haloproject.projectspartanv2.Fragments;

import android.os.Bundle;
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
public class CoolingFragment extends Fragment {
    private TempWheel waterTemp;
    private TextView flowPump;

    private AndroidBlue mAndroidBlue;
    private TopBar mTopBar;
    private DeviceHandlerCollection mDeviceHandlerCollection;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mTopBar = MainActivity.mTopBar;
        mAndroidBlue = AndroidBlue.getInstance();
        mDeviceHandlerCollection = DeviceHandlerCollection.getInstance();


        mTopBar.setMenuName("Cooling");
        View view = inflater.inflate(R.layout.fragment_cooling, container, false);
        waterTemp = (TempWheel) view.findViewById(R.id.waterTemp);
        flowPump = (TextView) view.findViewById(R.id.flowPump);

        //set onclick listeners
        view.findViewById(R.id.peltierauto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeviceHandlerCollection.peltier.auto();
            }
        });
        view.findViewById(R.id.peltieroff).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeviceHandlerCollection.peltier.off();
            }
        });
        view.findViewById(R.id.headfanson).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeviceHandlerCollection.headFans.on();
            }
        });
        view.findViewById(R.id.headfansoff).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeviceHandlerCollection.headFans.off();
            }
        });
        view.findViewById(R.id.waterpumpauto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeviceHandlerCollection.waterPump.auto();
            }
        });
        view.findViewById(R.id.waterpumpoff).setOnClickListener(new View.OnClickListener() {
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