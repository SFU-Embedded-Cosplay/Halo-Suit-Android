package com.haloproject.projectspartanv2.Fragments;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haloproject.bluetooth.AndroidBlueLe;
import com.haloproject.bluetooth.DeviceHandlerCollection;
import com.haloproject.projectspartanv2.MainActivity;
import com.haloproject.projectspartanv2.R;
import com.haloproject.projectspartanv2.view.MainButton;
import com.haloproject.projectspartanv2.view.TextDrawable;
import com.haloproject.projectspartanv2.view.TopBar;

import java.util.UUID;

/**
 * Fragment for displaying information about and controlling the gun.
 */
public class GunFragment extends Fragment {
    public static final String TAG = "GunFragment";
    private TopBar mTopBar;
    private AndroidBlueLe mAndroidBlueLe;
    private BluetoothGattCharacteristic ammoCharacteristic;
    private BluetoothGattCharacteristic commandCharacteristic;
    private DeviceHandlerCollection mDeviceHandlerCollection;
    private MainButton mReloadButton;
    private MediaPlayer mShotMediaPlayer;
    private MediaPlayer mReloadMediaPlayer;

    private static final String DEVICE_HANDLER_COLLECTION_KEY = "deviceHandlerCollection";
    private static final String BLE_DEVICE_ADDRESS = "98:4F:EE:0F:58:EC";
    private static final UUID GUN_SERVICE_UUID = UUID.fromString("19B10000-E8F2-537E-4F6C-D104768A1214");
    private static final UUID AMMO_CHARACTERISTIC_UUID = UUID.fromString("19B10001-E8F2-537E-4F6C-D104768A1214");
    private static final UUID COMMAND_CHARACTERISTIC_UUID = UUID.fromString("19B10002-E8F2-537E-4F6C-D104768A1214");

    // false for full auto, true for single shot
    private boolean firingMode = false;
    private static final byte RELOAD_COMMAND = 1;

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

        mAndroidBlueLe = new AndroidBlueLe();
        mAndroidBlueLe.initialize(getActivity(), getGattCallback());
        mDeviceHandlerCollection = (DeviceHandlerCollection) getArguments().getSerializable(DEVICE_HANDLER_COLLECTION_KEY);

        mShotMediaPlayer = MediaPlayer.create(getActivity(), R.raw.assault_rifle_shot);
        mReloadMediaPlayer = MediaPlayer.create(getActivity(), R.raw.assault_rifle_reload);

        mTopBar.setMenuName("Gun");

        View view = inflater.inflate(R.layout.fragment_gun, container, false);

        mReloadButton = (MainButton) view.findViewById(R.id.reloadButton);
        mReloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("GunFragment", "Reload");
                sendReloadCommand();
            }
        });

        View firingModeButton = view.findViewById(R.id.firingModeButton);
        //TODO: Add firing mode functionality using AndroidBlueLe
//        firingModeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MainButton button = (MainButton) v;
//                if (firingMode) {
//                    firingMode = false;
//                    mAndroidBlueUart.sendString("FullAuto");
//                    button.setIcon(getResources().getDrawable(R.drawable.speaker_on_icon));
//                    button.invalidate();
//                    Log.d("GunFragment", "Full auto");
//                }
//                else {
//                    firingMode = true;
//                    mAndroidBlueUart.sendString("SingleShot");
//                    button.setIcon(getResources().getDrawable(R.drawable.speaker_off_icon));
//                    button.invalidate();
//                    Log.d("GunFragment", "Single shot");
//                }
//
//            }
//        });

        return view;
    }

    private void sendReloadCommand() {
        if (commandCharacteristic != null) {
            commandCharacteristic.setValue(new byte[]{RELOAD_COMMAND});
            mAndroidBlueLe.writeCharacteristic(commandCharacteristic);
        }
    }

    private void connectToGunBleService() {
        BluetoothGattService gunService = mAndroidBlueLe.getGattService(GUN_SERVICE_UUID);
        if (gunService != null) {
            ammoCharacteristic = gunService.getCharacteristic(AMMO_CHARACTERISTIC_UUID);
            mAndroidBlueLe.setCharacteristicNotification(ammoCharacteristic, true);
            commandCharacteristic = gunService.getCharacteristic(COMMAND_CHARACTERISTIC_UUID);
            Log.d(TAG, "Connected to Gun Service and successfully fetched characteristics");
        }
        else {
            Log.d(TAG, "Failed to connect to Gun Service");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mAndroidBlueLe.connect(BLE_DEVICE_ADDRESS);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAndroidBlueLe.close();
    }

    private void changeAmmo(String ammoCount) {
        mReloadButton.setIcon(new TextDrawable(getResources(), ammoCount));

        // Don't play the shot sound after the gun is reloaded.
        // ie when the gun reloads, it sets ammo to 30, and should not shoot
        if (!ammoCount.equals("30")) {
            if (mShotMediaPlayer.isPlaying()) {
                mShotMediaPlayer.pause();
                mShotMediaPlayer.seekTo(0);
            }
            mShotMediaPlayer.start();
        }

        if (ammoCount.equals("0")) {
            mReloadMediaPlayer.start();
        }

        mReloadButton.invalidate();
    }

    public BluetoothGattCallback getGattCallback() {
        return new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    mAndroidBlueLe.setConnectionState(BluetoothProfile.STATE_CONNECTED);
                    Log.i(TAG, "Connected to GATT server.");
                    // Attempts to discover services after successful connection.
                    Log.i(TAG, "Attempting to start service discovery:" +
                            mAndroidBlueLe.discoverServices());

                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    mAndroidBlueLe.setConnectionState(BluetoothProfile.STATE_DISCONNECTED);
                    Log.i(TAG, "Disconnected from GATT server.");
                    mAndroidBlueLe.close();
                    mAndroidBlueLe.connect(BLE_DEVICE_ADDRESS);
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    connectToGunBleService();
                } else {
                    Log.w(TAG, "onServicesDiscovered received: " + status);
                }
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt,
                                             BluetoothGattCharacteristic characteristic,
                                             int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.d(TAG, "Characteristic read");
                }
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt,
                                                BluetoothGattCharacteristic characteristic) {
                Log.d(TAG, "Characteristic changed");
                if (characteristic.getUuid().equals(AMMO_CHARACTERISTIC_UUID)) {
                    Integer value = (int) characteristic.getValue()[0];
                    changeAmmo(value.toString());
                }
            }
        };
    }
}
