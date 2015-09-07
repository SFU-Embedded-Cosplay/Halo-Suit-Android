package com.haloproject.projectspartanv2.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.haloproject.bluetooth.AndroidBlue;
import com.haloproject.projectspartanv2.MainActivity;
import com.haloproject.projectspartanv2.R;
import com.haloproject.projectspartanv2.view.TopBar;

/**
 * Created by Tyler on 6/29/2015.
 */
public class SettingsFragment extends Fragment {
    private ListView btdevices;
    private View view;

    private TopBar mTopBar;
    private AndroidBlue mAndroidBlue;

    private static final String ANDROID_BLUE_KEY = "androidBlue";
    private static final String DEVICE_HANDLER_COLLECTION_KEY = "deviceHandlerCollection";

    public static SettingsFragment newInstance(AndroidBlue mAndroidBlue) {
        SettingsFragment fragment = new SettingsFragment();

        final Bundle args = new Bundle();

        args.putSerializable(ANDROID_BLUE_KEY, mAndroidBlue);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mTopBar = MainActivity.mTopBar;
        mAndroidBlue = (AndroidBlue) getArguments().getSerializable(ANDROID_BLUE_KEY);


        mTopBar.setMenuName("Settings");
        view = inflater.inflate(R.layout.fragment_settings, container, false);

        btdevices = (ListView) view.findViewById(R.id.btdevices);
        btdevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (mAndroidBlue.setBeagleBone(position)) {
                    mAndroidBlue.connect();
                }
            }
        });
        btdevices.setAdapter(mAndroidBlue.getDeviceStrings());
        view.findViewById(R.id.startdiscovery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAndroidBlue.startDiscovery();
            }
        });
        view.findViewById(R.id.lockconfiguration).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAndroidBlue.sendConfiguration();
            }
        });
        view.findViewById(R.id.deconfgiure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAndroidBlue.sendDeConfiguration();
            }
        });
        return view;
    }
}
