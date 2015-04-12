package com.haloproject.projectspartanv2;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;

import android.media.SoundPool;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;

import android.widget.TextView;


import com.haloproject.bluetooth.AndroidBlue;

import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends ActionBarActivity implements SensorEventListener {
    private AtomicBoolean isMicOn,isSoundOn;
    static private FragmentManager mFragmentManager;
    static private AndroidBlue mAndroidBlue;
    final int TOTAL_SWIPE_FRAGMENTS = 7;
    private boolean isUpRight = true;
    private WindowManager.LayoutParams params;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    static private int currentFragment; //-1 means its at main menu
    static private SharedPreferences mPreferences;
    private float x1, x2, y1, y2;
    static private TopBar mTopBar;
    Thread micThread;
    private SoundPool soundPool;

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
//        if (event.sensor == mSensor) {
//            final float z = event.values[2];
//
//            if (isUpRight) {
//                if (Math.abs(z) < 0.50) {
//                    isUpRight = false;
//                    params.screenBrightness = 0;
//                    getWindow().setAttributes(params);
//                    Log.d("App", "fellasleep");
//                }
//            } else {
//                if (Math.abs(z) >= 0.50) {
//                    isUpRight = true;
//                    params.screenBrightness = -1;
//                    getWindow().setAttributes(params);
//                    Log.d("App", "wokeup");
//                }
//            }
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SoundPool.Builder soundPoolBuilder = new SoundPool.Builder();
        soundPoolBuilder.setMaxStreams(4);
        soundPool = soundPoolBuilder.build();
        soundPool.load(this,R.raw.lights,1);
        soundPool.load(this,R.raw.shield_off,1);
        soundPool.load(this,R.raw.shield_on,1);

        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        int volume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        setContentView(R.layout.activity_main);
        //set up wakelock
        params = new WindowManager.LayoutParams();
        params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        getWindow().setAttributes(params);
        mFragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            mFragmentManager.beginTransaction()
                    .add(R.id.container, new MainFragment())
                    .commit();
            currentFragment = -1;
        }
        AndroidBlue.setContext(getApplicationContext());
        isMicOn = new AtomicBoolean(true);
        isSoundOn = new AtomicBoolean((true));
        mAndroidBlue = AndroidBlue.getInstance(isSoundOn,soundPool,volume);
        mPreferences = getPreferences(MODE_PRIVATE);
        if (mPreferences.contains("bluetooth")) {
            String device = mPreferences.getString("bluetooth", "");
            if (mAndroidBlue.setBeagleBone(device)) {
                mAndroidBlue.connect();
            }
        }
        mTopBar = (TopBar) findViewById(R.id.topbar);
        updateTopBar(mTopBar);

        //create sensor
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);

        //initialise microphone

        HandleMicrophoneRunnable handleMicrophoneRunnable = new HandleMicrophoneRunnable(isMicOn);
        micThread = new Thread(handleMicrophoneRunnable);
        micThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                y1 = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                y2 = event.getY();
                if (x2 - x1 > 600) {
                    if (currentFragment != -1 && currentFragment > 0) {
                        currentFragment -= 1;
                        mFragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                                .replace(R.id.container, swipeFragment(currentFragment))
                                .commit();
                    }
                } else if (x1 - x2 > 600) {
                    if (currentFragment != -1 && currentFragment < TOTAL_SWIPE_FRAGMENTS - 1) {
                        //left to right
                        currentFragment += 1;
                        mFragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                                .replace(R.id.container, swipeFragment(currentFragment))
                                .commit();
                    }
                } else if (y2 - y1 > 400) {
                    if (currentFragment != -1) {
                        currentFragment = -1;
                        mFragmentManager.beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .replace(R.id.container, swipeFragment(currentFragment))
                                .commit();
                    }
                }
                break;
        }
        return true;
    }

    private Fragment swipeFragment(int fragment) {
        switch (fragment) {
            case 0:
                return new VitalsFragment();
            case 1:
                return new CoolingFragment();
            case 2:
                return new LightingFragment();
            case 3:
                return new RadarFragment();
            case 4:
                return new BatteryFragment();
            case 5:
                return new WarningsFragment();
            case 6:
                return new SettingsFragment();
            default:
                return new MainFragment();
        }
    }

    private void openCurrentFragment() {
        mFragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .replace(R.id.container, swipeFragment(currentFragment))
                .commit();
    }

    private void toggleVoice(MainButton view) {
        boolean isMicOn = this.isMicOn.get();
        if(isMicOn)
        {
            this.isMicOn.set(false);
            view.setIcon(getResources().getDrawable(R.drawable.speaker_off_icon));
            view.invalidate();
        }
        else
        {
            this.isMicOn.set(true);
            view.setIcon(getResources().getDrawable(R.drawable.speaker_on_icon));
            view.invalidate();
        }
    }

    private void toggleSounds(MainButton view) {
        boolean isSoundOn = this.isSoundOn.get();
        if(isSoundOn)
        {
            this.isSoundOn.set(false);
            view.setIcon(getResources().getDrawable(R.drawable.music_off_icon));
            view.invalidate();
        }
        else
        {
            this.isSoundOn.set(true);
            view.setIcon(getResources().getDrawable(R.drawable.music_on_icon));
            view.invalidate();
        }
    }

    public void vitals(View view) {
        currentFragment = 0;
        openCurrentFragment();
    }

    public void cooling(View view) {
        currentFragment = 1;
        openCurrentFragment();
    }

    public void lighting(View view) {
        currentFragment = 2;
        openCurrentFragment();
    }

    public void radar(View view) {
        currentFragment = 3;
        openCurrentFragment();
    }

    public void batteries(View view) {
        currentFragment = 4;
        openCurrentFragment();
    }

    public void warnings(View view) {
        currentFragment = 5;
        openCurrentFragment();
    }

    public void settings(View view) {
        currentFragment = 6;
        openCurrentFragment();
    }
    public void voice(View view) {
        currentFragment = 7;
        toggleVoice((MainButton)view);
    }
    public void sounds(View view) {
        currentFragment = 8;
        toggleSounds((MainButton)view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    static private void updateTopBar(final TopBar mTopBar) {
        if (mAndroidBlue.isConnected()) {
            mTopBar.setBluetooth(true);
        } else {
            mTopBar.setBluetooth(false);
        }

        mAndroidBlue.setOnConnect(new Runnable() {
            @Override
            public void run() {
                mTopBar.setBluetooth(true);
            }
        });

        mAndroidBlue.setOnDisconnect(new Runnable()
        {
            @Override
            public void run()
            {
                mTopBar.setBluetooth(false);
            }
        });
    }

    static public class MainFragment extends Fragment {
        private LinearLayout mainMenu;
        private HorizontalScrollView scrollView;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            currentFragment = -1;
            mTopBar.setMenuName("Main Menu");
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.fragment_main, container, false);
            mainMenu = (LinearLayout) view.findViewById(R.id.mainmenu);
            scrollView = (HorizontalScrollView) view.findViewById(R.id.scrollview);
            return view;
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            mAndroidBlue.changeOnReceive();
        }
    }

    static public class CoolingFragment extends Fragment {
        TempWheel waterTemp;
        TextView flowPump;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            mTopBar.setMenuName("Cooling");
            View view = inflater.inflate(R.layout.fragment_cooling, container, false);
            waterTemp = (TempWheel) view.findViewById(R.id.waterTemp);
            flowPump = (TextView) view.findViewById(R.id.flowPump);
            mAndroidBlue.setOnReceive(new Runnable() {
                @Override
                public void run() {
                    waterTemp.setTemp(mAndroidBlue.waterTemperature.getValue());
                    int flow = mAndroidBlue.flowRate.getValue();
                    String newFlow = String.format("%d", flow);

                    flowPump.setText(newFlow);
                }
            });
            return view;
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            mAndroidBlue.changeOnReceive();
        }
    }

    static public class LightingFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            mTopBar.setMenuName("Lighting");
            View view = inflater.inflate(R.layout.fragment_lighting, container, false);
            view.findViewById(R.id.mainlightson).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAndroidBlue.mainLights.on();
                }
            });
            view.findViewById(R.id.mainlightsoff).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAndroidBlue.mainLights.off();
                }
            });
            view.findViewById(R.id.mainlightsauto).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAndroidBlue.mainLights.auto();
                }
            });
            view.findViewById(R.id.redlightson).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAndroidBlue.redHeadLight.on();
                }
            });
            view.findViewById(R.id.redlightsoff).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAndroidBlue.redHeadLight.off();
                }
            });
            view.findViewById(R.id.whitelightson).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAndroidBlue.whiteHeadLight.on();
                }
            });
            view.findViewById(R.id.whitelightsoff).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAndroidBlue.whiteHeadLight.off();
                }
            });
            return view;
        }
    }

    static public class RadarFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            mTopBar.setMenuName("Radar");
            View view = inflater.inflate(R.layout.fragment_radar, container, false);
            return view;
        }
    }


    static public class VitalsFragment extends Fragment {
        private TempWheel headTemp;
        private TempWheel armpitsTemp;
        private TempWheel crotchTemp;
        private TextView heartRate;

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            mTopBar.setMenuName("Vitals");
            View view = inflater.inflate(R.layout.fragment_vitals, container, false);
            headTemp = (TempWheel) view.findViewById(R.id.headTemp);
            armpitsTemp = (TempWheel) view.findViewById(R.id.armpitsTemp);
            crotchTemp = (TempWheel) view.findViewById(R.id.crotchTemp);
            heartRate = (TextView) view.findViewById(R.id.heartRate);

            mAndroidBlue.setOnReceive(new Runnable() {
                @Override
                public void run() {
                    headTemp.setTemp(mAndroidBlue.headTemperature.getValue());
                    armpitsTemp.setTemp(mAndroidBlue.armpitsTemperature.getValue());
                    crotchTemp.setTemp(mAndroidBlue.crotchTemperature.getValue());

                    int hr = mAndroidBlue.heartRate.getValue();
                    String heartrate = String.format("%d", hr);
                    heartRate.setText(heartrate);
                }
            });

            return view;
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            mAndroidBlue.changeOnReceive();
        }
    }

    static public class SettingsFragment extends Fragment {
        private ListView btdevices;
        private View view;

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
            return view;
        }

        @Override
        public void onStart() {
            super.onStart();
            if (mAndroidBlue.isEnabled()) {
                mAndroidBlue.startDiscovery();
            } else {
                mAndroidBlue.enableBluetooth(getActivity());
                mAndroidBlue.startDiscovery();
            }

        }
    }

    static public class WarningsFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            mTopBar.setMenuName("Warnings");
            View view = inflater.inflate(R.layout.fragment_warnings, container, false);
            return view;
        }
    }

    static public class BatteryFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            mTopBar.setMenuName("Batteries");
            View view = inflater.inflate(R.layout.fragment_battery, container, false);
            return view;
        }
    }

    static public class Warning extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.warning, container, false);
            return view;
        }

        @Override
        public void onStart() {
            super.onStart();

        }
    }
}