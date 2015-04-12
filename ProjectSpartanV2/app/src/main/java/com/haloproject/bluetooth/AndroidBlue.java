package com.haloproject.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Looper;
import android.widget.ArrayAdapter;

import com.haloproject.projectspartanv2.SoundMessageHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Adam Brykajlo on 18/02/15.
 */
public class AndroidBlue {
    private final int REQUEST_ENABLE_BT = 42;

    private BluetoothSocket mSocket;
    private BluetoothAdapter mAdapter;
    private ArrayAdapter<BluetoothDevice> mDevices;
    private ArrayAdapter<String> mDeviceStrings;
    private BluetoothDevice mBeagleBone;
    private JSONObject mJSON;
    private Handler mHandler;

    private Runnable onConnect;
    private Runnable onDisconnect;
    private Runnable onReceive;

    //batteries
    public final BeagleIntegerOutput battery8AH;
    public final BeagleIntegerOutput battery2AH;
    public final BeagleIntegerOutput batteryAndroid;
    public final BeagleIntegerOutput batteryGlass;

    //lights
    public final BeagleAutoSwitch mainLights;
    public final BeagleSwitch redHeadLight;
    public final BeagleSwitch whiteHeadLight;

    //cooling
    //input
    public final BeagleAutoOffSwitch peltier;
    public final BeagleAutoOffSwitch waterPump;
    public final BeagleSwitch headFans;
    //output
    public final BeagleIntegerOutput flowRate;
    public final BeagleDoubleOutput waterTemperature;

    //vitals
    public final BeagleDoubleOutput headTemperature;
    public final BeagleDoubleOutput crotchTemperature;
    public final BeagleDoubleOutput armpitsTemperature;
    public final BeagleIntegerOutput heartRate;

    static private AndroidBlue mAndroidBlue = null;
    static private Context mContext;

    private AtomicBoolean isSoundOn;
    private SoundPool soundPool;
    private int volume;

    private AndroidBlue(AtomicBoolean isSoundOn,SoundPool soundPool,int volume) {
        this.isSoundOn = isSoundOn;
        this.soundPool = soundPool;
        this.volume = volume;

        mAdapter = BluetoothAdapter.getDefaultAdapter();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mContext.registerReceiver(mReceiver, filter);
        mDevices = new ArrayAdapter<BluetoothDevice>(mContext, android.R.layout.simple_list_item_1);
        mDeviceStrings = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1);

        headTemperature = new BeagleDoubleOutput("head temperature");
        crotchTemperature = new BeagleDoubleOutput("crotch temperature");
        armpitsTemperature = new BeagleDoubleOutput("armpits temperature");
        waterTemperature = new BeagleDoubleOutput("water temperature");

        flowRate = new BeagleIntegerOutput("flow rate");
        heartRate = new BeagleIntegerOutput("heart rate");
        battery2AH = new BeagleIntegerOutput("2 AH battery");
        battery8AH = new BeagleIntegerOutput("8 AH battery");
        batteryAndroid = new BeagleIntegerOutput("phone battery");
        batteryGlass = new BeagleIntegerOutput("hud battery");


        redHeadLight = new BeagleSwitch("head lights red");
        whiteHeadLight = new BeagleSwitch("head lights white");
        peltier = new BeagleAutoOffSwitch("peltier");
        waterPump = new BeagleAutoOffSwitch("water pump");
        headFans = new BeagleSwitch("head fans");
        mainLights = new BeagleAutoSwitch("lights");

        mHandler = new Handler(Looper.getMainLooper());
    }

    static public void setContext(Context context) {
        mContext = context;
    }

    static public AndroidBlue getInstance(AtomicBoolean isSoundOn,SoundPool soundPool,int volume) {
        if (mContext != null) {
            if (mAndroidBlue == null) {
                mAndroidBlue = new AndroidBlue(isSoundOn,soundPool,volume);
            }
            return mAndroidBlue;
        }
        return null;
    }

    public boolean isEnabled() {
        return mAdapter.isEnabled();
    }

    public void enableBluetooth(Activity callingActivity) {
        if (!isEnabled()) {
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            callingActivity.startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
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
            }
            //clear list adapters
            mDevices.clear();
            mDeviceStrings.clear();

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

                    new Thread(new ConnectedRunnable()).start();
                } catch (Exception e) {

                }
            }
        }
    }

    private class DisconnectedRunnable implements Runnable {
        @Override
        public void run() {
            mHandler.post(onDisconnect);
            while (true) {
                try {
                    Method m = mBeagleBone.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                    mSocket = (BluetoothSocket) m.invoke(mBeagleBone, 3);

                    mSocket.connect();

                    new Thread(new ConnectedRunnable()).start();
                    return;
                } catch (Exception e) {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e2) {

                    }
                }
            }
        }
    }

    private class ConnectedRunnable implements Runnable {
        private byte[] mBytes;

        @Override
        public void run() {
            mHandler.post(onConnect);
            while (true) {
                try {
                    mBytes = new byte[528];
                    mSocket.getInputStream().read(mBytes);
                    mJSON = new JSONObject(new String(mBytes));
                    if(isSoundOn.get())
                    {//a copy is needed because the object is passed off to a separate thread
                        JSONObject jsonCopy = new JSONObject(new String(mBytes));
                        SoundMessageHandler.handleSoundMessage(jsonCopy,soundPool,volume);
                    }

                    mHandler.post(onReceive);
                } catch (IOException e) {
                    try {
                        new Thread(new DisconnectedRunnable()).start();
                        mSocket.close();
                        return;
                    } catch (Exception io) {
                        //don't return will probably close on next loop
                    }
                } catch (JSONException e) {

                }
            }
        }
    }

    public void setOnConnect(Runnable onConnect) {
        this.onConnect = onConnect;
    }
    public void setOnDisconnect(Runnable onDisconnect) {
        this.onDisconnect = onDisconnect;
    }
    public void setOnReceive(Runnable onReceive) {
        this.onReceive = onReceive;
    }

    public void changeUI() {
        this.onReceive = null;
        this.onDisconnect = null;
        this.onConnect = null;
    }

    public void changeOnReceive() {
        onReceive = null;
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
    
    public class BeagleDoubleOutput {
        public BeagleDoubleOutput(String location) {
            this.location = location;
        }

        private String location;

        public double getValue() {
            try {
                return mJSON.getDouble(location); //get double could accept and integer because double is bigger than an integer. thus this code will work for both ints and doubles
            } catch (Exception e) {
                return -1000.0;
            }
        }
    }

    public class BeagleIntegerOutput {
        public BeagleIntegerOutput(String location) {
            this.location = location;
        }

        private String location;

        public int getValue() {
            try {
                return mJSON.getInt(location); //get double could accept and integer because double is bigger than an integer. thus this code will work for both ints and doubles
            } catch (Exception e) {
                return -1;
            }
        }
    }

    abstract private class BeagleInputHandler {
        private BeagleInputHandler(String location) {
            this.location = location;
        }

        protected String location;

        protected void setState(String state) {
            try {
                JSONObject switchObject = new JSONObject();
                switchObject.put(location, state);
                mSocket.getOutputStream().write(switchObject.toString().getBytes());
            } catch (Exception e) {

            }
        }

        protected boolean isStateSet(String state) {
            try {
                return mJSON.getString(location).equals(state);
            } catch (Exception e) {
                return false;
            }
        }
    }

    //turn switched auto or off
    //TODO ask chris if we can information about on or off state
    public class BeagleAutoOffSwitch extends BeagleInputHandler {
        public BeagleAutoOffSwitch(String location) {
            super(location);
        }

        public void auto() {
            setState("auto");
        }

        public void off() {
            setState("off");
        }

        public boolean isAuto() {
            return isStateSet("auto");
        }

        public boolean isOff() {
            return isStateSet("off");
        }
    }

    //turns things on or off or auto
    public class BeagleAutoSwitch extends BeagleInputHandler {
        public BeagleAutoSwitch(String location) {
            super(location);
        }

        public void auto() {
            setState("auto");
        }

        public void on() {
            setState("on");
        }

        public void off() {
            setState("off");
        }

        public boolean isAuto() {
            return isStateSet("auto");
        }

        public boolean isOn() {
            return isStateSet("on");
        }

        public boolean isOff() {
            return isStateSet("off");
        }
    }

    //used for turning things on or off on the beaglebone
    public class BeagleSwitch extends BeagleInputHandler {
        public BeagleSwitch(String location) {
            super(location);
        }

        public void on() {
            setState("on");
        }

        public void off() {
            setState("off");
        }

        public boolean isOn() {
            return isStateSet("on");
        }

        public boolean isOff() {
            return isStateSet("off");
        }
    }
}