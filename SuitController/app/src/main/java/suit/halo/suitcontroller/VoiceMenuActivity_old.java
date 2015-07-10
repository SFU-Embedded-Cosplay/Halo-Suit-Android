package suit.halo.suitcontroller;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Process;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.glass.eye.EyeGesture;
import com.google.android.glass.eye.EyeGestureManager;
import com.google.android.glass.eye.EyeGestureManager.Listener;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Set;
import java.util.UUID;

public class VoiceMenuActivity_old extends Activity implements VoiceDetection.VoiceDetectionListener
{
    public static volatile boolean connectedToPhone =  false;
    public static volatile boolean soundEnabled =  true;
    private VoiceDetection mVoiceDetection;
    private HeadListView mScroll;
    private EyeGestureManager mEyeGestureManager;
    private WinkEyeGestureListener mWinkEyeGestureListener;
    private DoubleBlinkEyeGestureListener mDoubleBlinkEyeGestureListener;
    private EyeGesture winkGesture = EyeGesture.WINK;
    private EyeGesture doubleBlinkGesture = EyeGesture.DOUBLE_BLINK;
    public String firstWord = "";

    private TextView tv0, tv1, tv2, tv3;

    private BluetoothDevice mDevice;
    private BluetoothSocket mSocket;
    private BluetoothDevice phoneDevice;
    private BluetoothSocket phoneSocket;

    private BluetoothAdapter mAdapter;
    private Set<BluetoothDevice> mPairedDevices;

    AudioRecord recorder;
    AudioTrack player;

    private int sampleRate = 8000;
    private int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    int minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
    private boolean status = true;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice_menu);

        Constants.initializeConstants();

        mEyeGestureManager = EyeGestureManager.from(this);
        mWinkEyeGestureListener = new WinkEyeGestureListener();
        mDoubleBlinkEyeGestureListener = new DoubleBlinkEyeGestureListener();

        mVoiceDetection = new VoiceDetection(this, Constants.OK_GLASS, this, getResources().getStringArray(R.array.junk_words));

        mScroll = (HeadListView) findViewById(R.id.hotword_chooser);
        String[] words = Constants.getFirstWords();
        IconList adapter = new IconList(this, words);
        mScroll.setAdapter(adapter);

        tv0 = (TextView) findViewById(R.id.temp1);
        tv1 = (TextView) findViewById(R.id.temp2);
        tv2 = (TextView) findViewById(R.id.temp3);
        tv3 = (TextView) findViewById(R.id.temp4);

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
                if(deviceName.equals(Constants.getPhoneDeviceIdentifier()))
                {
                    this.phoneDevice = mDevice;
                }
            }

            new connectToHost().start();
            new connectToPhone().start();
        }
        catch(Exception e)
        {
            int x = 1;
        }
    }

    public void startStreaming()
    {
        Thread streamThread = new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                Thread prevThread = null;
                Process.setThreadPriority(Process.THREAD_PRIORITY_LOWEST);
                try
                {
                    byte[] buffer = new byte[minBufSize];

                    recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, audioFormat, minBufSize);
                    recorder.startRecording();


                    while (true)
                    {
                        minBufSize = recorder.read(buffer, 0, buffer.length);
                        int l = buffer.length;
                        if(connectedToPhone && soundEnabled)
                        {
                            final int USHORT_MASK = (1 << 16) - 1;

                            final ByteBuffer buf = ByteBuffer.wrap(buffer).order(
                                    ByteOrder.LITTLE_ENDIAN);
                            final ByteBuffer newBuf = ByteBuffer.allocate(
                                    buffer.length).order(ByteOrder.LITTLE_ENDIAN);

                            int sample;
                            while (buf.hasRemaining()) {
                                sample = (int) buf.getShort() & USHORT_MASK;
                                sample *= 8;//manually multiply recording volume
                                newBuf.putShort((short) (sample & USHORT_MASK));
                            }

                            buffer = newBuf.array();

                            phoneSocket.getOutputStream().write(buffer);
                        }
                    }


                } catch (Exception e)
                {
                }

            }

        });
        streamThread.start();
    }

    private class connectToHost extends Thread
    {
        @Override
        public void run()
        {
            try
            {
                Method m = mDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                mSocket = (BluetoothSocket) m.invoke(mDevice, Constants.DEVICE_CHANNEL);
                mSocket.connect();

                new BeagleBoneReceivingThread().start();
            } catch (Exception e)
            {
                int x = 1;
            }
        }
    }

    private class connectToPhone extends Thread
    {
        @Override
        public void run()
        {
            try
            {
                phoneSocket = phoneDevice.createRfcommSocketToServiceRecord(UUID.fromString(Constants.audioDeviceUuid));
                phoneSocket.connect();
                connectedToPhone = true;
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

        mEyeGestureManager.register(doubleBlinkGesture, mDoubleBlinkEyeGestureListener);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        mEyeGestureManager.unregister(winkGesture, mWinkEyeGestureListener);

        mEyeGestureManager.unregister(winkGesture, mWinkEyeGestureListener);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_CENTER));
        dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_CENTER));
        mVoiceDetection.changePhrases(Constants.VOICE_MENU_MODE.KEYWORD);
        mVoiceDetection.start();

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mVoiceDetection.stop();
    }

    @Override
    public void onHotwordDetected()
    {
        processCommand("Command mode");
        mVoiceDetection.changePhrases(Constants.VOICE_MENU_MODE.FIRST_LEVEL);
    }

    @Override
    public void onFirstWordDetected(int index, String phrase)
    {
        firstWord = phrase;

        mScroll.setAdapter(new ArrayAdapter<>(this, R.layout.voice_menu_item, Constants.getSecondWords(phrase)));

        mVoiceDetection.changePhrases(Constants.VOICE_MENU_MODE.SECOND_LEVEL, phrase);
    }

    @Override
    public void onSecondWordDetected(int index, String phrase)
    {
        processCommand(firstWord + " " + phrase);

        mScroll.setAdapter(new ArrayAdapter<>(this, R.layout.voice_menu_item, Constants.getFirstWords()));

        mVoiceDetection.changePhrases(Constants.VOICE_MENU_MODE.KEYWORD, phrase);
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
                    String selectedItem = mScroll.getSelectedItem().toString();
                    if((mVoiceDetection.mode == Constants.VOICE_MENU_MODE.FIRST_LEVEL) || (mVoiceDetection.mode == Constants.VOICE_MENU_MODE.KEYWORD))
                    {
                        firstWord = selectedItem;
                        IconList adapter = new IconList(VoiceMenuActivity_old.this, Constants.getSecondWords(selectedItem));
                        mScroll.setAdapter(adapter);
                        mVoiceDetection.changePhrases(Constants.VOICE_MENU_MODE.SECOND_LEVEL, selectedItem);
                    }
                    else
                    {//second level

                        processCommand(firstWord + " " + selectedItem);
                        IconList adapter = new IconList(VoiceMenuActivity_old.this, Constants.getFirstWords());
                        mScroll.setAdapter(adapter);
                        mVoiceDetection.changePhrases(Constants.VOICE_MENU_MODE.KEYWORD, selectedItem);
                    }
                }
            });

        }
    }

    private class DoubleBlinkEyeGestureListener implements Listener
    {

        @Override
        public void onEnableStateChange(EyeGesture eyeGesture, boolean paramBoolean)
        {
        }

        @Override
        public void onDetected(final EyeGesture eyeGesture)
        {
            soundEnabled = !soundEnabled;
        }
    }

    private class SendAudioToPhoneThread extends Thread
    {
        byte[] buffer;
        Thread prevThread;
        public SendAudioToPhoneThread(byte[] buffer,Thread prevThread)
        {
            this.buffer = buffer;
            this.prevThread = prevThread;
        }
        @Override
        public void run()
        {
            super.run();
            Process.setThreadPriority(Process.THREAD_PRIORITY_LOWEST);
            if(prevThread != null)
            {
                try
                {
                    prevThread.join();
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            try
            {
                phoneSocket.getOutputStream().write(buffer);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }


    private class BeagleBoneReceivingThread extends Thread
    {
        private byte[] bytes;
        private double temp0, temp1, temp2, temp3;

        @Override
        public void run()
        {
            while (true)
            {
                try
                {

                    bytes = new byte[1024];
                    int i = mSocket.getInputStream().read(bytes);
                    JSONObject j = new JSONObject(new String(bytes));
                    temp0 = j.getDouble("head temperature");
                    temp1 = j.getDouble("armpits temperature");
                    temp2 = j.getDouble("crotch temperature");
                    temp3 = j.getDouble("water temperature");
                    if(i != -1)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                tv0.setText(String.format(" %.1f", temp0));
                                tv1.setText(String.format(" %.1f", temp1));
                                tv2.setText(String.format(" %.1f", temp2));
                                tv3.setText(String.format(" %.1f", temp3));
                            }
                        });
                    }

                } catch (Exception e)
                {

                }

            }
        }
    }

    private class PhoneReceivingThread extends Thread
    {
        private byte[] bytes;
        private double temp0, temp1, temp2, temp3;

        @Override
        public void run()
        {
            while (true)
            {
                try
                {

                    bytes = new byte[1024];
                    int i = mSocket.getInputStream().read(bytes);
                    JSONObject j = new JSONObject(new String(bytes));
                    temp0 = j.getDouble("head temperature");
                    temp1 = j.getDouble("armpits temperature");
                    temp2 = j.getDouble("crotch temperature");
                    temp3 = j.getDouble("water temperature");
                    if(i != -1)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                tv0.setText(String.format(" %.1f", temp0));
                                tv1.setText(String.format(" %.1f", temp1));
                                tv2.setText(String.format(" %.1f", temp2));
                                tv3.setText(String.format(" %.1f", temp3));
                            }
                        });
                    }

                } catch (Exception e)
                {

                }

            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return true;
    }

    public void processCommand(String command)
    {
        switch (command)
        {
            case "lights on":
            {
                try
                {
                    JSONObject j = new JSONObject();
                    j.put("lights", "on");
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
                    mSocket.getOutputStream().write(j.toString().getBytes());
                } catch (Exception e)
                {

                }
            }
            break;
            case "cooling_on on":
            {
                try
                {
                    JSONObject j = new JSONObject();
                    j.put("peltier", "on");
                    j.put("water fan", "on");
                    j.put("water pump", "on");
                    j.put("head fans", "on");
                    mSocket.getOutputStream().write(j.toString().getBytes());
                } catch (Exception e)
                {

                }
            }
            break;
            case "cooling_on off":
            {
                try
                {
                    JSONObject j = new JSONObject();
                    j.put("peltier", "off");
                    j.put("water fan", "off");
                    j.put("water pump", "off");
                    j.put("head fans", "off");
                    mSocket.getOutputStream().write(j.toString().getBytes());
                } catch (Exception e)
                {

                }
            }
            break;
        }
    }


}