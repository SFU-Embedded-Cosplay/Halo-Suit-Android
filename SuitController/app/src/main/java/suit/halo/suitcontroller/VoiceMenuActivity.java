package suit.halo.suitcontroller;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.glass.eye.EyeGesture;
import com.google.android.glass.eye.EyeGestureManager;
import com.google.android.glass.eye.EyeGestureManager.Listener;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Set;

public class VoiceMenuActivity extends Activity implements SensorEventListener
{
    private static final float INVALID_YAW = 10;
    private long cumulativeYaw = 0;

    private Sensor mSensor;
    private int mLastAccuracy;
    private SensorManager mSensorManager;
    private static final int SENSOR_RATE_uS = 400000;


    private EyeGestureManager mEyeGestureManager;
    private WinkEyeGestureListener mWinkEyeGestureListener;
    private EyeGesture winkGesture = EyeGesture.WINK;

    private TextView temp1Text, temp2Text, temp3Text, temp4Text, batt1Text, batt2Text, batt3Text, batt4Text;
    private LinearLayout blink1, blink2, blink3, blink4, blink5;
    private ImageView blinkOn1, blinkOn2, blinkOn3, blinkOn4, blinkOn5;

    private BluetoothDevice mDevice;
    private BluetoothSocket mSocket;

    private BluetoothAdapter mAdapter;
    private Set<BluetoothDevice> mPairedDevices;

    private Thread connectToHostThread, beagleBoneReceivingThread, batteryStateThread;

    private long lastYaw = -1;
    private long lastSelectedButton = -1;
    private int selectedButton = 1;
    private int[] selectedButtonState = new int[5];

    private SoundPool soundPool;
    private int volume;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice_menu);

        Constants.initializeConstants();

        mEyeGestureManager = EyeGestureManager.from(this);
        mWinkEyeGestureListener = new WinkEyeGestureListener();

        temp1Text = (TextView) findViewById(R.id.temp1);
        temp2Text = (TextView) findViewById(R.id.temp2);
        temp3Text = (TextView) findViewById(R.id.temp3);
        temp4Text = (TextView) findViewById(R.id.temp4);

        batt1Text = (TextView) findViewById(R.id.batt1);
        batt2Text = (TextView) findViewById(R.id.batt2);
        batt3Text = (TextView) findViewById(R.id.batt3);
        batt4Text = (TextView) findViewById(R.id.batt4);

        blink1 = (LinearLayout) findViewById(R.id.micLayout);
        blink2 = (LinearLayout) findViewById(R.id.soundLayout);
        blink3 = (LinearLayout) findViewById(R.id.headLightsLayout);
        blink4 = (LinearLayout) findViewById(R.id.coolingLayout);
        blink5 = (LinearLayout) findViewById(R.id.lightsLayout);

        blinkOn1 = (ImageView) findViewById(R.id.micButton);
        blinkOn2 = (ImageView) findViewById(R.id.soundButton);
        blinkOn3 = (ImageView) findViewById(R.id.headLightsButton);
        blinkOn4 = (ImageView) findViewById(R.id.coolingButton);
        blinkOn5 = (ImageView) findViewById(R.id.lightsButton);

        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        volume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
        soundPool.load(this, R.raw.shield_off, 1);
        soundPool.load(this, R.raw.shield_on, 1);
    }

    private class ConnectToHostThread extends Thread
    {
        @Override
        public void run()
        {
            try
            {
                Method m = mDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                mSocket = (BluetoothSocket) m.invoke(mDevice, Constants.DEVICE_CHANNEL);
                mSocket.connect();

                beagleBoneReceivingThread = new BeagleBoneReceivingThread();
                beagleBoneReceivingThread.start();
            } catch (Exception e)
            {
                int x = 1;
            }
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        mEyeGestureManager.register(winkGesture, mWinkEyeGestureListener);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        lastYaw = -1;
        mSensorManager.registerListener(this, mSensor, SENSOR_RATE_uS);

        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mAdapter.enable();

        try
        {
            mPairedDevices = mAdapter.getBondedDevices();
            for (BluetoothDevice mDevice : mPairedDevices)
            {
                String deviceName = mDevice.getName();
                if(deviceName.equals(Constants.DEVICE_IDENTIFIER))
                {
                    this.mDevice = mDevice;
                }
            }

            connectToHostThread = new ConnectToHostThread();
            connectToHostThread.start();
            batteryStateThread = new BatteryStateThread();
            batteryStateThread.start();
        } catch (Exception e)
        {
            int x = 1;
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        mEyeGestureManager.unregister(winkGesture, mWinkEyeGestureListener);
        beagleBoneReceivingThread.stop();
        batteryStateThread.stop();
        try
        {
            mSocket.close();
            mAdapter.disable();
        } catch (IOException e)
        {
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    private class WinkEyeGestureListener implements Listener
    {

        @Override
        public void onEnableStateChange(EyeGesture eyeGesture, boolean paramBoolean)
        {
        }

        @Override
        public void onDetected(final EyeGesture eyeGesture)
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    winkOnButton();
                }
            });

        }
    }


    private class BeagleBoneReceivingThread extends Thread
    {
        private byte[] bytes;
        private double temp1 = 666;
        private double temp2 = 666;
        private double temp3 = 666;
        private double temp4 = 666;

        private int batt1 = 666;
        private int batt2 = 666;
        private int batt3 = 666;
        private int batt4 = 666;

        boolean lowbat = false;

        @Override
        public void run()
        {
            while (true)
            {
                try
                {

                    bytes = new byte[1024];
                    int i = mSocket.getInputStream().read(bytes);
                    JSONObject jsonObject = new JSONObject(new String(bytes));
                    if(jsonObject.has("head temperature"))
                    {
                        temp1 = jsonObject.getDouble("head temperature");
                    }
                    if(jsonObject.has("armpits temperature"))
                    {
                        temp2 = jsonObject.getDouble("armpits temperature");
                    }
                    if(jsonObject.has("crotch temperature"))
                    {
                        temp3 = jsonObject.getDouble("crotch temperature");
                    }
                    if(jsonObject.has("water temperature"))
                    {
                        temp4 = jsonObject.getDouble("water temperature");
                    }
                    if(jsonObject.has("water temperature"))
                    {
                        temp4 = jsonObject.getDouble("water temperature");
                    }

                    if(jsonObject.has("8 AH battery"))
                    {
                        batt1 = jsonObject.getInt("8 AH battery");
                    }
                    if(jsonObject.has("2 AH battery"))
                    {
                        batt2 = jsonObject.getInt("2 AH battery");
                    }
                    if(jsonObject.has("hud battery"))
                    {
                        batt3 = jsonObject.getInt("hud battery");
                    }
                    if(jsonObject.has("phone battery"))
                    {
                        batt4 = jsonObject.getInt("phone battery");
                    }
                    if(jsonObject.has("warnings"))
                    {
                        JSONObject warnings = jsonObject.getJSONObject("warnings");
                        if(warnings.has("low 8AH battery warning")||(warnings.has("low 2AH battery warning")))
                        {
                            SoundMessageHandler.handleSoundMessage("low_bat",soundPool,volume);
                        }
                    }

                    if(i != -1)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                if(temp1 < 666)
                                {
                                    temp1Text.setText(String.format("H: %.1f", temp1));
                                }
                                if(temp2 < 666)
                                {
                                    temp2Text.setText(String.format("A: %.1f", temp2));
                                }
                                if(temp3 < 666)
                                {
                                    temp3Text.setText(String.format("C: %.1f", temp3));
                                }
                                if(temp4 < 666)
                                {
                                    temp4Text.setText(String.format("W: %.1f", temp4));
                                }
                                if(batt1 < 666)
                                {
                                    batt1Text.setText(String.format(" 8 AH battery: %d", batt1));
                                }
                                if(batt2 < 666)
                                {
                                    batt2Text.setText(String.format(" 2 AH battery: %d", batt2));
                                }
                                if(temp4 < 666)
                                {
                                    batt3Text.setText(String.format("  Hud battery: %d", batt3));
                                }
                                if(temp4 < 666)
                                {
                                    batt4Text.setText(String.format("Phone battery: %d", batt4));
                                }
                            }
                        });
                    }

                } catch (Exception e)
                {

                }

            }
        }
    }

    private class BatteryStateThread extends Thread
    {
        @Override
        public void run()
        {
            while (true)
            {
                try
                {
                    IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                    Intent batteryStatus = registerReceiver(null, ifilter);
                    int charge = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

                    processCommand("sendBatteryState", charge);
                    Thread.sleep(20000, 0);
                } catch (Exception e)
                {
                    int x = 1;
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return true;
    }

    public void processCommand(String command, int charge)
    {
        switch (command)
        {
            case "lights on":
            {
                try
                {
                    JSONObject j = new JSONObject();
                    j.put("lights", "on");
                    j.put("play sound", "lights");
                    mSocket.getOutputStream().write(j.toString().getBytes());
                } catch (Exception e)
                {

                }
            }
            break;
            case "lights off":
            {
                try
                {
                    JSONObject j = new JSONObject();
                    j.put("lights", "off");
                    j.put("play sound", "lights");
                    mSocket.getOutputStream().write(j.toString().getBytes());
                } catch (Exception e)
                {

                }
            }
            break;
            case "lights auto":
            {
                try
                {
                    JSONObject j = new JSONObject();
                    j.put("lights", "auto");
                    j.put("play sound", "lights");
                    mSocket.getOutputStream().write(j.toString().getBytes());
                } catch (Exception e)
                {

                }
            }
            break;
            case "head lights on":
            {
                try
                {
                    JSONObject j = new JSONObject();
                    j.put("head lights red", "on");
                    j.put("head lights white", "on");
                    j.put("play sound", "lights");
                    mSocket.getOutputStream().write(j.toString().getBytes());
                } catch (Exception e)
                {

                }
            }
            break;
            case "head lights off":
            {
                try
                {
                    JSONObject j = new JSONObject();
                    j.put("head lights red", "off");
                    j.put("head lights white", "off");
                    j.put("play sound", "lights");
                    mSocket.getOutputStream().write(j.toString().getBytes());
                } catch (Exception e)
                {

                }
            }
            break;
            case "cooling on":
            {
                try
                {
                    JSONObject j = new JSONObject();
                    j.put("peltier", "on");
                    j.put("water fan", "on");
                    j.put("water pump", "on");
                    j.put("head fans", "on");
                    j.put("play sound", "shield_on");
                    mSocket.getOutputStream().write(j.toString().getBytes());
                } catch (Exception e)
                {

                }
            }
            break;
            case "cooling off":
            {
                try
                {
                    JSONObject j = new JSONObject();
                    j.put("peltier", "off");
                    j.put("water fan", "off");
                    j.put("water pump", "off");
                    j.put("head fans", "off");
                    j.put("play sound", "shield_off");
                    mSocket.getOutputStream().write(j.toString().getBytes());
                } catch (Exception e)
                {

                }
            }
            break;
            case "sendBatteryState":
            {
                try
                {
                    JSONObject j = new JSONObject();
                    j.put("hud battery", charge);
                    mSocket.getOutputStream().write(j.toString().getBytes());
                } catch (Exception e)
                {
                    int x = 1;
                }
            }
            break;

            case "voice on":
            {
                try
                {
                    JSONObject j = new JSONObject();
                    j.put("play sound", "voice_on");
                    mSocket.getOutputStream().write(j.toString().getBytes());
                } catch (Exception e)
                {

                }
            }
            break;
            case "voice off":
            {
                try
                {
                    JSONObject j = new JSONObject();
                    j.put("play sound", "voice_off");
                    mSocket.getOutputStream().write(j.toString().getBytes());
                } catch (Exception e)
                {

                }
            }
            break;

            case "audio on":
            {
                try
                {
                    JSONObject j = new JSONObject();
                    j.put("play sound", "audio_on");
                    mSocket.getOutputStream().write(j.toString().getBytes());
                } catch (Exception e)
                {

                }
            }
            break;
            case "audio off":
            {
                try
                {
                    JSONObject j = new JSONObject();
                    j.put("play sound", "audio_off");
                    mSocket.getOutputStream().write(j.toString().getBytes());
                } catch (Exception e)
                {

                }
            }
            break;
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event)
    {
        float[] mat = new float[9],
                orientation = new float[3];
        long position, old;

        if(mLastAccuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
        {
            return;
        }

        SensorManager.getRotationMatrixFromVector(mat, event.values);
        SensorManager.getOrientation(mat, orientation);
        double yaw = (orientation[0] + Math.PI) * 100000;
        long intYaw = (int) yaw;

        if(lastYaw == INVALID_YAW)
        {
            lastYaw = intYaw;
            lastSelectedButton = selectedButton;
        }

        long difference = lastYaw - intYaw;

        lastYaw = intYaw;

        if(Math.abs(difference) < 1000)//ignore wrapAround
        {
            cumulativeYaw += difference;
        }

        if(cumulativeYaw > 18000)
        {
            selectedButton--;
            cumulativeYaw = 0;
        }
        else if(cumulativeYaw < -18000)
        {
            selectedButton++;
            cumulativeYaw = 0;
        }
        if(selectedButton > getCount())
        {
            selectedButton--;
        }
        if(selectedButton < 1)
        {
            selectedButton++;
        }
        if(selectedButton != lastSelectedButton)
        {
            lastSelectedButton = selectedButton;
            highlightButton(selectedButton);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        mLastAccuracy = accuracy;
    }

    private int getCount()
    {//number of blink buttons
        return 5;
    }

    private void highlightButton(int buttonNum)
    {
        blink1.setBackgroundColor(Color.TRANSPARENT);
        blink2.setBackgroundColor(Color.TRANSPARENT);
        blink3.setBackgroundColor(Color.TRANSPARENT);
        blink4.setBackgroundColor(Color.TRANSPARENT);
        blink5.setBackgroundColor(Color.TRANSPARENT);

        switch (buttonNum)
        {
            case 1:
            {
                blink1.setBackgroundColor(Color.GRAY);
                break;
            }
            case 2:
            {
                blink2.setBackgroundColor(Color.GRAY);
                break;
            }
            case 3:
            {
                blink3.setBackgroundColor(Color.GRAY);
                break;
            }
            case 4:
            {
                blink4.setBackgroundColor(Color.GRAY);
                break;
            }
            case 5:
            {
                blink5.setBackgroundColor(Color.GRAY);
                break;
            }
            default:
            {
                int shouldntHappen = 1;
                break;
            }
        }
    }

    private void winkOnButton()
    {
        switch (selectedButton)
        {
            case 1:
            {
                winkOnVoiceButton();
                break;
            }
            case 2:
            {
                winkOnSoundButton();
                break;
            }
            case 3:
            {
                winkOnHeadLightsButton();
                break;
            }
            case 4:
            {
                winkOnCoolingButton();
                break;
            }
            case 5:
            {
                winkOnLightsButton();
                break;
            }
            default:
            {
                int shouldntHappen = 1;
                break;
            }
        }
    }

    private void winkOnVoiceButton()
    {
        if(selectedButtonState[0] == 0)
        {
            blinkOn1.setBackground(getResources().getDrawable(R.drawable.voice_off));
            selectedButtonState[0] = 1;
            processCommand("voice off", 0);
        }
        else
        {
            blinkOn1.setBackground(getResources().getDrawable(R.drawable.voice_on));
            selectedButtonState[0] = 0;
            processCommand("voice on", 0);
        }
    }

    private void winkOnSoundButton()
    {
        if(selectedButtonState[1] == 0)
        {
            blinkOn2.setBackground(getResources().getDrawable(R.drawable.audio_off));
            selectedButtonState[1] = 1;
            processCommand("audio off", 0);
        }
        else
        {
            blinkOn2.setBackground(getResources().getDrawable(R.drawable.audio_on));
            selectedButtonState[1] = 0;
            processCommand("audio on", 0);
        }
    }

    private void winkOnHeadLightsButton()
    {
        if(selectedButtonState[2] == 0)
        {
            blinkOn3.setBackground(getResources().getDrawable(R.drawable.head_lights_off));
            selectedButtonState[2] = 1;
            processCommand("head lights off", 1);
        }
        else
        {
            blinkOn3.setBackground(getResources().getDrawable(R.drawable.head_lights));
            selectedButtonState[2] = 0;
            processCommand("head lights on", 1);
        }
    }

    private void winkOnCoolingButton()
    {
        if(selectedButtonState[3] == 0)
        {
            blinkOn4.setBackground(getResources().getDrawable(R.drawable.cooling_off));
            selectedButtonState[3] = 1;
            processCommand("cooling off", 0);
        }
        else
        {
            blinkOn4.setBackground(getResources().getDrawable(R.drawable.cooling_on));
            selectedButtonState[3] = 0;
            processCommand("cooling on", 0);
        }
    }

    private void winkOnLightsButton()
    {
        if(selectedButtonState[4] == 0)
        {
            blinkOn5.setBackground(getResources().getDrawable(R.drawable.lights_auto));
            selectedButtonState[4] = 1;
            processCommand("lights auto", 0);
        }
        else if(selectedButtonState[4] == 1)
        {
            blinkOn5.setBackground(getResources().getDrawable(R.drawable.lights_off));
            selectedButtonState[4] = 2;
            processCommand("lights off", 0);
        }
        else
        {
            blinkOn5.setBackground(getResources().getDrawable(R.drawable.lights_on));
            selectedButtonState[4] = 0;
            processCommand("lights on", 0);
        }
    }
}