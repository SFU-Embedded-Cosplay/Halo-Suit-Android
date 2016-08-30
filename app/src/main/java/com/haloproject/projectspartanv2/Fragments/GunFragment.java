package com.haloproject.projectspartanv2.Fragments;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haloproject.bluetooth.AndroidBlueUart;
import com.haloproject.bluetooth.DeviceHandlerCollection;
import com.haloproject.projectspartanv2.MainActivity;
import com.haloproject.projectspartanv2.R;
import com.haloproject.projectspartanv2.view.MainButton;
import com.haloproject.projectspartanv2.view.TextDrawable;
import com.haloproject.projectspartanv2.view.TopBar;

/**
 * Created by AlexLand on 2016-01-12.
 */
public class GunFragment extends Fragment implements AndroidBlueUart.Callback {
    private TopBar mTopBar;
    private AndroidBlueUart mAndroidBlueUart;
    private DeviceHandlerCollection mDeviceHandlerCollection;
    private MainButton mReloadButton;

    private static final String ANDROID_BLUE_UART_KEY = "androidBlueUart";
    private static final String DEVICE_HANDLER_COLLECTION_KEY = "deviceHandlerCollection";

    // false for full auto, true for single shot
    private boolean firingMode = false;

    public static GunFragment newInstance(DeviceHandlerCollection mDeviceHandlerCollection) {
        GunFragment fragment = new GunFragment();

        final Bundle args = new Bundle();

        args.putSerializable(DEVICE_HANDLER_COLLECTION_KEY, mDeviceHandlerCollection);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mTopBar = MainActivity.mTopBar;

        mAndroidBlueUart = new AndroidBlueUart(getActivity());
        mDeviceHandlerCollection = (DeviceHandlerCollection) getArguments().getSerializable(DEVICE_HANDLER_COLLECTION_KEY);

        mTopBar.setMenuName("Gun");

        View view = inflater.inflate(R.layout.fragment_gun, container, false);

        mReloadButton = (MainButton) view.findViewById(R.id.reloadButton);
        mReloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("GunFragment", "Reload");
                mAndroidBlueUart.sendString("Reload");
            }
        });

        View firingModeButton = view.findViewById(R.id.firingModeButton);
        firingModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainButton button = (MainButton) v;
                if (firingMode) {
                    firingMode = false;
                    mAndroidBlueUart.sendString("FullAuto");
                    button.setIcon(getResources().getDrawable(R.drawable.speaker_on_icon));
                    button.invalidate();
                    Log.d("GunFragment", "Full auto");
                }
                else {
                    firingMode = true;
                    mAndroidBlueUart.sendString("SingleShot");
                    button.setIcon(getResources().getDrawable(R.drawable.speaker_off_icon));
                    button.invalidate();
                    Log.d("GunFragment", "Single shot");
                }

            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mAndroidBlueUart.registerCallback(this);

        // Scan will automatically connect to BT adapter based on address defined at
        // AndroidBlueUart.BLE_UART_ADAPTER_ADDRESS
        mAndroidBlueUart.startScan();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAndroidBlueUart.unregisterCallback(this);
        mAndroidBlueUart.disconnect();
    }

    @Override
    public void onConnected(AndroidBlueUart uart) {
        Log.d("GunFragment", "Bluetooth LE UART connected!");
    }

    @Override
    public void onConnectFailed(AndroidBlueUart uart) {
        Log.d("GunFragment", "Bluetooth LE UART connection failed.");
    }

    @Override
    public void onDisconnected(AndroidBlueUart uart) {
        Log.d("GunFragment", "Bluetooth LE UART disconnected.");
    }

    @Override
    public void onReceive(AndroidBlueUart uart, BluetoothGattCharacteristic rx) {
        final String receivedMessage = rx.getStringValue(0).trim();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                changeAmmo(receivedMessage);
            }
        });
        Log.v("GunFragment", "Received: " + receivedMessage);
    }

    @Override
    public void onDeviceFound(BluetoothDevice device) {
        Log.v("GunFragment", "Found device:\nName: " + device.getName() + "\nAddress:  " + device.getAddress() + "\n");
    }

    @Override
    public void onDeviceInfoAvailable() {
        Log.v("GunFragment", "Device info: " + mAndroidBlueUart.getDeviceInfo());
    }

    private void changeAmmo(String ammoCount) {
        mReloadButton.setIcon(new TextDrawable(getResources(), ammoCount));
        mReloadButton.invalidate();
    }
}
