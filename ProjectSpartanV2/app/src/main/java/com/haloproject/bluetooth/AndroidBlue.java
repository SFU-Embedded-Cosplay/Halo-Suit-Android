package com.haloproject.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by Adam Brykajlo on 18/02/15.
 */
public class AndroidBlue {
    private BluetoothSocket mSocket;
    private BluetoothAdapter mAdapter;
    private ArrayAdapter<BluetoothDevice> mDevices;
    private final int REQUEST_ENABLE_BT = 13;
    private ArrayAdapter<String> mDeviceStrings;
    private Runnable onConnect;
    private Runnable onReceive;
    private BluetoothDevice mBeagleBone;
    private JSONObject mJSON;
    private Handler mHandler;
    static private AndroidBlue mAndroidBlue = null;
    static private Context mContext;
    static private Activity mActivity;

    public final Temperature headTemperature;
    public final Temperature crotchTemperature;
    public final Temperature armpitsTemperature;
    public final Temperature waterTemperature;
    public final Switch redHeadLight;
    public final Switch whiteHeadLight;
    public final Switch peltier;
    public final Switch waterPump;
    public final Switch headFans;
    public final Switch mainLights;


    protected AndroidBlue() {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mContext.registerReceiver(mReceiver, filter);
        mDevices = new ArrayAdapter<BluetoothDevice>(mContext, android.R.layout.simple_list_item_1);
        mDeviceStrings = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1);
        headTemperature = new Temperature("head temperature");
        crotchTemperature = new Temperature("crotch temperature");
        armpitsTemperature = new Temperature("armpits temperature");
        waterTemperature = new Temperature("water temperature");
        redHeadLight = new Switch("head lights red");
        whiteHeadLight = new Switch("head lights white");
        peltier = new Switch("peltier");
        waterPump = new Switch("water pump");
        headFans = new Switch("head fans");
        mainLights = new Switch("lights") {
            public void auto() {
                try {
                    JSONObject switchObject = new JSONObject();
                    switchObject.put(this.location, "auto");
                    mSocket.getOutputStream().write(switchObject.toString().getBytes());
                } catch (Exception e) {

                }
            }
        };

        mHandler = new Handler(Looper.getMainLooper());
    }

    static public void setContext(Context context) {
        mContext = context;
    }

    static public void setActivity(Activity activity) {
        mActivity = activity;
    }

    static public AndroidBlue getInstance() {
        if (mContext != null && mActivity != null) {
            if (mAndroidBlue == null) {
                mAndroidBlue = new AndroidBlue();
            }
            return mAndroidBlue;
        }
        return null;
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

    public boolean setBeagleBone(int pos) {
        if (pos < mDevices.getCount()) {
            mBeagleBone = mDevices.getItem(pos);
            return true;
        }
        return false;
    }

    public boolean setBeagleBone(String device) {
        try {
            mBeagleBone = mAdapter.getRemoteDevice(device);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public BluetoothDevice getBeagleBone() {
        return mBeagleBone;
    }

    public void connect() {
        new Thread(new ConnectRunnable()).start();
    }

    public boolean sendConfiguration() {
        if (isConnected()) {
            try {
                if (mBeagleBone != null) {
                    JSONObject configuration = new JSONObject();

                    JSONObject android = new JSONObject();
                    android.put("android", mAdapter.getAddress());
                    configuration.put("configuration", android);

                    mSocket.getOutputStream().write(configuration.toString().getBytes());
                }
            } catch (Exception e) {
                return false;
            }
            return true;
        }
        return false;
    }

    public boolean sendDeConfiguration() {
        if (isConnected()) {
            try {
                if (mBeagleBone != null) {
                    JSONObject deconfiguration = new JSONObject();
                    JSONObject android = new JSONObject();
                    android.put("android", "delete");
                    deconfiguration.put("configuration", android);
                    mSocket.getOutputStream().write(deconfiguration.toString().getBytes());
                }
            } catch (Exception e) {
                return false;
            }
            return true;
        }
        return false;
    }

    private class ConnectRunnable implements Runnable {
        @Override
        public void run() {
            if (mBeagleBone != null) {
                try {
                    Method m = mBeagleBone.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                    mSocket = (BluetoothSocket) m.invoke(mBeagleBone, 3);

                    mSocket.connect();

                    mHandler.post(onConnect);
                    new Thread(new ConnectedRunnable()).start();
                } catch (Exception e) {

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "Could Not Connect to " + mBeagleBone, Toast.LENGTH_LONG).show();
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

            while (isConnected()) {
                try {
                    mBytes = new byte[528];
                    mSocket.getInputStream().read(mBytes);
                    mJSON = new JSONObject(new String(mBytes));
                    mHandler.post(onReceive);
                } catch (Exception e) {

                }
            }
        }
    }


    public void setOnConnect(Runnable onConnect) {
        this.onConnect = onConnect;
    }
    public void setOnReceive(Runnable onReceive) {
        this.onReceive = onReceive;
    }

    public void destroyOnReceive() {
        this.onReceive = null;
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

    public class Temperature {
        public Temperature(String location) {
            this.location = location;
        }

        protected String location;

        public double getValue() {
            try {
                return mJSON.getDouble(location);
            } catch (Exception e) {
                return -1000.0;
            }
        }
    }

    //used for turning things on or off on the beaglebone
    public class Switch {
        public Switch(String location) {
            this.location = location;
        }

        protected String location;

        public void on() {
            try {
                JSONObject switchObject = new JSONObject();
                switchObject.put(location, "on");
                mSocket.getOutputStream().write(switchObject.toString().getBytes());
            } catch (Exception e) {

            }
        }

        public void off() {
            try {
                JSONObject switchObject = new JSONObject();
                switchObject.put(location, "off");
                mSocket.getOutputStream().write(switchObject.toString().getBytes());
            } catch (Exception e) {

            }
        }
    }
}