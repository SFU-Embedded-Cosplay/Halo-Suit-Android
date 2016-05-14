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
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.haloproject.bluetooth.BluetoothInterfaces.JSONCommunicationDevice;
import com.haloproject.projectspartanv2.SoundMessageHandler;
import com.haloproject.projectspartanv2.Warning;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Adam Brykajlo on 18/02/15.
 */
public class AndroidBlue implements JSONCommunicationDevice, Serializable {
    private final int REQUEST_ENABLE_BT = 42;

    private List<Warning> mWarnings;

    private BluetoothSocket mSocket;
    private BluetoothAdapter mAdapter;
    private ArrayAdapter<BluetoothDevice> mDevices;
    private ArrayAdapter<String> mDeviceStrings;
    private BluetoothDevice mBeagleBone;
    private JSONObject mJSON;
    private Handler mHandler; //handles the launching of threads (runnables)

    private Socket mTestSocket = new Socket();
    private InetSocketAddress mTestSocketAddress = new InetSocketAddress("10.0.2.2", 8080);
    private static boolean isTestingWithSocket = false;
    // use ctrl + F11 to rotate android emulator sideways

    private Runnable onConnect;
    private Runnable onDisconnect;
    private Runnable onReceive;
    private Runnable onWarning;

                                                    // TODO: prefix static variables with s if we want to follow the m prefix standard.
    private static AndroidBlue mAndroidBlue = null; // following the m prefix standard static variables should be prefixed with s.
    private static Context mContext;                // see http://source.android.com/source/code-style.html#follow-field-naming-conventions
                                                    // which has a section on "Follow Field Naming Conventions" which explains the convention.
    private boolean isSoundOn;
    private SoundPool soundPool;
    private int volume;

    private AndroidBlue(SoundPool soundPool, int volume, Context context) {
        mContext = context;

        this.isSoundOn = false;
        this.soundPool = soundPool;
        this.volume = volume;

        mAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mAdapter == null) {
            // bluetooth is not supported for the device.
            // this is likely an emulator, so switch to debug mode.
            isTestingWithSocket = true;
        }

        // enableBluetooth();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mContext.registerReceiver(mReceiver, filter);

        mDevices = new ArrayAdapter<BluetoothDevice>(mContext, android.R.layout.simple_list_item_1);
        mDeviceStrings = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1);

        mHandler = new Handler(Looper.getMainLooper());
        mWarnings = new LinkedList<Warning>();
    }

    public boolean isConnected() {
        if(isTestingWithSocket) {
            if(mTestSocket != null) {
                return mTestSocket.isConnected();
            }
        }

        if (mSocket != null) {
            return mSocket.isConnected();
        }
        return false;
    }

    public static boolean isTestingWithSocket() {
        return isTestingWithSocket;
    }

    public boolean isSoundOn() {
        return isSoundOn;
    }

    public ArrayAdapter<String> getDeviceStrings() {
        return mDeviceStrings;
    }

    public void turnSoundOff() {
        isSoundOn = false;
    }

    public void turnSoundOn() {
        isSoundOn = true;
    }

    public static AndroidBlue getInstance(SoundPool soundPool, int volume) {
        if (mContext != null) {
            if (mAndroidBlue == null) {
                mAndroidBlue = new AndroidBlue(soundPool,volume, mContext);
            }
            return mAndroidBlue;
        }
        return null;
    }

    public static void setContext(Context context) {
        mContext = context;
    }

    public boolean isEnabled() {
        if(isTestingWithSocket) {
            return true;
        }

        return mAdapter.isEnabled();
    }

    public void enableBluetooth(Activity callingActivity) {
        assert false;
        //TODO: find out what this does.
        //this does not appear to get called.

        if(isTestingWithSocket) {
            return;
        }

        if (!isEnabled()) {
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            callingActivity.startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
        }
    }

    public void disableBluetooth() {
        if(isTestingWithSocket) {
            return;
        }

        if (isEnabled()) {
            mAdapter.disable();
        }
    }

    public boolean startDiscovery() {

        if(isTestingWithSocket) {
            connect();
            return true;
        }

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
        if(isTestingWithSocket) {
            return true;
        }

        if (pos < mDevices.getCount()) {
            mBeagleBone = mDevices.getItem(pos);
            return true;
        }
        return false;
    }

    public boolean setBeagleBone(String device) {
        if(isTestingWithSocket) {
            return true;
        }

        try {
            mBeagleBone = mAdapter.getRemoteDevice(device);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public void connect() {
        new Thread(new ConnectRunnable()).start();
    }

    public boolean sendConfiguration() {
        if (isConnected()) {
            try {
                //if (mBeagleBone != null) { //I dont think this is necessary
                JSONObject configuration = new JSONObject();

                JSONObject android = new JSONObject();
                android.put("android", getAdapterAddress());
                configuration.put("configuration", android);

                getOutputStream().write(configuration.toString().getBytes());
                //}
            } catch (Exception e) {
                return false;
            }
            return true;
        }
        return false;
    }

    //should this be named delete or disconnectConfiguration???
    public boolean sendDeConfiguration() {
        if (isConnected()) {
            try {
                //if (mBeagleBone != null) { //TODO: question - is this check necessary?

                    JSONObject deconfiguration = new JSONObject();
                    JSONObject android = new JSONObject();

                    android.put("android", "delete");
                    deconfiguration.put("configuration", android);

                    getOutputStream().write(deconfiguration.toString().getBytes());
                //}
            } catch (Exception e) {
                return false;
            }
            return true;
        }
        return false;
    }


    // 4 classes that correspond to different runnables
    private class ConnectRunnable implements Runnable {
        @Override
        public void run() {
            //TODO: connect socket

            //if (mBeagleBone != null) { //probably not necessary
                try {
                    if(isTestingWithSocket) {
                        mTestSocket.connect(mTestSocketAddress);
                    } else {
                        Method m = mBeagleBone.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                        mSocket = (BluetoothSocket) m.invoke(mBeagleBone, 3);

                        mSocket.connect();
                    }

                    new Thread(new ConnectedRunnable()).start();
                    new Thread(new BatteryRunnable()).start();
                } catch (Exception e) {

                }
            //}
        }
    }

    private class BatteryRunnable implements Runnable {
        @Override
        public void run() {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

            while (isConnected()) {
                Intent batteryStatus = mContext.registerReceiver(null, ifilter);
                int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

                int charge = (int) ((float) level / (float) scale * 100);

                try {
                    JSONObject battery = new JSONObject();
                    battery.put("phone battery", charge);
                    getOutputStream().write(battery.toString().getBytes());
                    Thread.sleep(5000);
                } catch (Exception e) {

                }
            }
        }
    }

    /**
     * identical to connectRunnable except posting onDisconnect
     * */
    private class DisconnectedRunnable implements Runnable {
        @Override
        public void run() {
            mHandler.post(onDisconnect);
            while (true) {
                try {
                    if(isTestingWithSocket) {
                        mTestSocket.connect(mTestSocketAddress);
                    } else {
                        // question: why is this connecting agian???
                        Method m = mBeagleBone.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                        mSocket = (BluetoothSocket) m.invoke(mBeagleBone, 3);

                        mSocket.connect();
                    }

                    new Thread(new ConnectedRunnable()).start();
                    return;
                } catch (Exception e) {
                    //question Why do were wait here???
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
            //mHandler.post(new BatteryRunnable());
            mHandler.post(onConnect);
            while (true) {
                try {
                    mBytes = new byte[1024];
                    mAndroidBlue.getInputStream().read(mBytes);
                    mJSON = new JSONObject(new String(mBytes));
                    if(isSoundOn) {
                        //a copy is needed because the object is passed off to a separate thread
                        JSONObject jsonCopy = new JSONObject(new String(mBytes));
                        SoundMessageHandler.handleSoundMessage(jsonCopy, soundPool, volume);
                    }
                    setWarnings();
                    mHandler.post(onReceive);
                    Log.d("JSON", mJSON.toString());
                } catch (IOException e) {
                    try {
                        new Thread(new DisconnectedRunnable()).start();
                        if(isTestingWithSocket) {
                            mTestSocket.close();
                        } else {
                            mSocket.close();
                        }
                        return;
                    } catch (Exception io) {
                        //don't return will probably close on next loop
                    }
                } catch (JSONException e) {
                    Log.d("JSON", e.getLocalizedMessage());
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
    public void setOnWarning(Runnable onWarning) {
        this.onWarning = onWarning;
    }

    //TODO: can we remove this?
    //is this for resetting purposes?
    public void changeUI() {
        this.onReceive = null;
        this.onDisconnect = null;
        this.onConnect = null;
    }

    public void changeOnReceive() {
        onReceive = null;
    }

    public ArrayAdapter<Warning> getWarnings() {
        ArrayAdapter<Warning> warnings = new ArrayAdapter<Warning>(mContext, android.R.layout.simple_list_item_1);
        for (Warning warning : mWarnings) {
            warnings.add(warning);
        }
        return warnings;
    }

    //TODO: should probably rename this to comething more comprehensive
    private void setWarnings() {
        try {
            boolean newWarning = false;

            JSONObject warnings = mJSON.getJSONObject("warnings");
            for (Warning warning : Warning.values()) {
                if (warnings.has(warning.toString())) {
                    if (!mWarnings.contains(warning)) {
                        mWarnings.add(warning);
                        newWarning = true;
                    }
                } else {
                    if (mWarnings.contains(warning)) {
                        mWarnings.remove(warning);
                    }
                }
            }

            if (newWarning) {
                mHandler.post(onWarning);
            }
        } catch (Exception e) {

        }
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

    /**
     * returns a reference to the JSON object inside androidBlue.
     * not safe because we are returning a reference.
     * */
    public JSONObject getJSON() {
        //is JSONObject immutable? -> No I don't think so
        return mJSON; //TODO: look into returning a copy (how will this effect performance)
    }

    public OutputStream getOutputStream() throws IOException {
        if(isTestingWithSocket) {
            return mTestSocket.getOutputStream();
        }

        return mSocket.getOutputStream();
    }

    public InputStream getInputStream() throws IOException {
        if(isTestingWithSocket) {
            return mTestSocket.getInputStream();
        }

        return mSocket.getInputStream();

    }

    private String getAdapterAddress() {
        if(isTestingWithSocket) {
            return mTestSocketAddress.getAddress().toString();
        }

        return mAdapter.getAddress();
    }
}