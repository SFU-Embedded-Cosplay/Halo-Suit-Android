package com.haloproject.projectspartanv2.Fragments;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haloproject.bluetooth.AndroidBlue;
import com.haloproject.bluetooth.AndroidBlueUart;
import com.haloproject.bluetooth.DeviceHandlerCollection;
import com.haloproject.projectspartanv2.MainActivity;
import com.haloproject.projectspartanv2.R;
import com.haloproject.projectspartanv2.view.TopBar;

import org.json.JSONException;

/**
 * Created by AlexLand on 2016-01-12.
 */
public class GunFragment extends Fragment implements AndroidBlueUart.Callback {
    private TopBar mTopBar;
    private AndroidBlue mAndroidBlue;
    private DeviceHandlerCollection mDeviceHandlerCollection;
    private AndroidBlueUart mUart;

    private static final String ANDROID_BLUE_KEY = "androidBlue";
    private static final String DEVICE_HANDLER_COLLECTION_KEY = "deviceHandlerCollection";

    public static GunFragment newInstance(AndroidBlue mAndroidBlue, DeviceHandlerCollection mDeviceHandlerCollection) {
        GunFragment fragment = new GunFragment();

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

        mUart = new AndroidBlueUart(getActivity());

        mTopBar.setMenuName("Gun");

        View view = inflater.inflate(R.layout.fragment_gun, container, false);

        View reloadButton = view.findViewById(R.id.reloadButton);
        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("GunFragment", "Reload clicked");
                //TODO: send "Reload" text command
                sendString("Reload");
            }
        });

        View singleShotButton = view.findViewById(R.id.singleShotButton);
        singleShotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("GunFragment", "Single shot clicked");
                //TODO: send Single Shot Mode text command
            }
        });

        View fullAutoButton = view.findViewById(R.id.fullAutoButton);
        fullAutoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("GunFragment", "Full auto clicked");
                //TODO: send Full Auto Mode text command
            }
        });

        return view;
    }

    private void sendString(String message) {
        StringBuilder stringBuilder = new StringBuilder();

        // We can only send 20 bytes per packet, so break longer messages
        // up into 20 byte payloads
        int len = message.length();
        int pos = 0;
        while(len != 0) {
            stringBuilder.setLength(0);
            if (len>=20) {
                stringBuilder.append(message.toCharArray(), pos, 20 );
                len-=20;
                pos+=20;
            }
            else {
                stringBuilder.append(message.toCharArray(), pos, len);
                len = 0;
            }
            mUart.send(stringBuilder.toString());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mUart.registerCallback(this);

        // Scan will automatically connect to BT adapter based on address defined at
        // AndroidBlueUart.BLE_UART_ADAPTER_ADDRESS
        mUart.startScan();
    }

    @Override
    public void onStop() {
        super.onStop();
        mUart.unregisterCallback(this);
        mUart.disconnect();
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
        Log.d("GunFragment", "Received: " + rx.getStringValue(0));
    }

    @Override
    public void onDeviceFound(BluetoothDevice device) {
        Log.d("GunFragment", "Found device:\nName: " + device.getName() + "\nAddress:  " + device.getAddress() + "\n");
    }

    @Override
    public void onDeviceInfoAvailable() {
        Log.d("GunFragment", "Device info: " + mUart.getDeviceInfo());
    }
}
