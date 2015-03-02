package com.haloproject.projectspartanv2;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.json.JSONObject;

import java.lang.reflect.Method;

/**
 * Created by Adam Brykajlo on 18/02/15.
 */
public class AndroidBlue {
    private BluetoothSocket mSocket;
    private BluetoothAdapter mAdapter;
    private ArrayAdapter<BluetoothDevice> mDevices;
    private BluetoothDevice mDevice;
    private final int REQUEST_ENABLE_BT = 13;
    private Context mContext;
    private Activity mActivity;
    private ArrayAdapter<String> mDeviceStrings;
    private Runnable onConnect;
    private BluetoothDevice mBeagleBone;
    private BluetoothDevice mGoogleGlass;
    static private JSONObject mJSON;


    public AndroidBlue(Activity activity, Context context) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mContext = context;
        mActivity = activity;
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mContext.registerReceiver(mReceiver, filter);
        mDevices = new ArrayAdapter<BluetoothDevice>(mContext, android.R.layout.simple_list_item_1);
        mDeviceStrings = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1);
    }

    public boolean isEnabled() {
        return mAdapter.isEnabled();
    }

    public void enableBluetooth() {
        if (!isEnabled()) {
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivity.startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
        }
    }

    public void disableBluetooth() {
        if (isEnabled()) {
            mAdapter.disable();
        }
    }

    public boolean isConnected() {
        if (mSocket != null) {
            return mSocket.isConnected();
        }
        return false;
    }

    public ArrayAdapter<String> getDeviceStrings() {
        return mDeviceStrings;
    }

    public boolean startDiscovery() {
        if (isEnabled()) {
            if (mAdapter.isDiscovering()) {
                mAdapter.cancelDiscovery();
                mDevices.clear();
                mDeviceStrings.clear();
            }
            return mAdapter.startDiscovery();
        }
        return false;
    }

    public boolean setDevice(int pos) {
        if (pos < mDevices.getCount()) {
            mDevice = mDevices.getItem(pos);
            return true;
        }
        return false;
    }

    public boolean setBeagleBone(int pos) {
        if (pos < mDevices.getCount()) {
            mBeagleBone = mDevices.getItem(pos);
            return true;
        }
        return false;
    }

    public boolean setGoogleGlass(int pos) {
        if (pos < mDevices.getCount()) {
            mGoogleGlass = mDevices.getItem(pos);
            return true;
        }
        return false;
    }

    public BluetoothDevice getBeagleBone() {
        return mBeagleBone;
    }

    public BluetoothDevice getGoogleGlass() {
        return mGoogleGlass;
    }

    public void connect() {
        new Thread(new ConnectRunnable()).start();
    }

    public boolean sendConfiguration() {
        if (isConnected()) {
            try {
                JSONObject configuration = new JSONObject();
                if (mGoogleGlass != null) {
                    JSONObject googleglass = new JSONObject();
                    googleglass.put("glass", mGoogleGlass.getAddress());
                    configuration.put("configuration", googleglass);
                }

                JSONObject android = new JSONObject();
                android.put("android", mAdapter.getAddress());
                configuration.put("configuration", android);

                mSocket.getOutputStream().write(configuration.toString().getBytes());
            } catch (Exception e) {

            }
            return true;
        }
        return false;
    }

    private class ConnectRunnable implements Runnable {
        @Override
        public void run() {
            if (mDevice != null) {
                try {
                    Method m = mDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                    mSocket = (BluetoothSocket) m.invoke(mDevice, 3);

                    mSocket.connect();

                    new Thread(onConnect).start();
                    new Thread(new ConnectedRunnable()).start();
                } catch (Exception e) {

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "Could Not Connect to " + mDevice, Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }
        }
    }

    private class ConnectedRunnable implements Runnable {
        private byte[] mBytes;

        @Override
        public void run() {
            mBytes = new byte[528];
            while (isConnected()) {
                try {
                    mSocket.getInputStream().read(mBytes);
                    mJSON = new JSONObject(mBytes.toString());
                } catch (Exception e) {

                }
            }
        }
    }

    public void setOnConnect(Runnable onConnect) {
        this.onConnect = onConnect;
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mDeviceStrings.add(device.getName() + "\n" + device.getAddress());
                // Add the name and address to an array adapter to show in a ListView
                mDevices.add(device);
            }
        }
    };

    public enum Tempurature {
        HEAD("head temperature"),
        BODY("body temperature"),
        CROTCH("crotch temperature"),
        ARMPITS("armpits temperature"),
        WATER("water temperature");


        private Tempurature(String location) {
            this.location = location;
        }

        private String location;

        public double getValue() {
            try {
                return mJSON.getDouble(location);
            } catch (Exception e) {

            }
            return 0;
        }
    }
}