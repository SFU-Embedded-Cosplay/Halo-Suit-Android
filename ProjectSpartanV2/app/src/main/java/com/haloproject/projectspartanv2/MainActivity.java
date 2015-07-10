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
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.haloproject.bluetooth.AndroidBlue;
import com.haloproject.projectspartanv2.Fragments.BatteryFragment;
import com.haloproject.projectspartanv2.Fragments.CoolingFragment;
import com.haloproject.projectspartanv2.Fragments.LightingFragment;
import com.haloproject.projectspartanv2.Fragments.RadarFragment;
import com.haloproject.projectspartanv2.Fragments.SettingsFragment;
import com.haloproject.projectspartanv2.Fragments.VitalsFragment;
import com.haloproject.projectspartanv2.view.BatteryBar;
import com.haloproject.projectspartanv2.view.MainButton;
import com.haloproject.projectspartanv2.view.TempWheel;
import com.haloproject.projectspartanv2.view.TopBar;

public class MainActivity extends ActionBarActivity implements SensorEventListener {
    private static FragmentManager mFragmentManager;
    private static AndroidBlue mAndroidBlue;

    public static final int TOTAL_SWIPE_FRAGMENTS = 7;

    private WindowManager.LayoutParams params;

    private SensorManager mSensorManager;
    private Sensor mSensor;

    private static int currentFragment; //-1 means its at main menu

    private static SharedPreferences mPreferences;
    private float x1, x2, y1, y2;
    //TODO: make mTopBar private and find a way so that all the fragments don't have to access it directly
    public static TopBar mTopBar; //cant pass this directly into a view with a bundle since it is not serializable or parcable

    private MicrophoneHandler mMicrophoneHandler;
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

        setupAudioServices();
        int volume = getVolume();

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

        mMicrophoneHandler = MicrophoneHandler.getInstance();
        AndroidBlue.setContext(getApplicationContext());
        mAndroidBlue = AndroidBlue.getInstance(soundPool,volume);
        mPreferences = getPreferences(MODE_PRIVATE);
        if (mPreferences.contains("bluetooth")) {
            String device = mPreferences.getString("bluetooth", "");
            if (mAndroidBlue.setBeagleBone(device)) {
                mAndroidBlue.connect();
            }
        }
        mTopBar = (TopBar) findViewById(R.id.topbar);
        updateTopBar(mTopBar);

        if (!mAndroidBlue.isEnabled()) {
            mAndroidBlue.enableBluetooth(this);
        }

        //set on warning
        mAndroidBlue.setOnWarning(new Runnable() {
            @Override
            public void run() {
                currentFragment = 5;
                mFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_down, R.anim.slide_out_down)
                        .replace(R.id.container, swipeFragment(5))
                        .commit();
            }
        });

        //create sensor
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
    }

    private void setupAudioServices() {
        SoundPool.Builder soundPoolBuilder = new SoundPool.Builder();
        soundPoolBuilder.setMaxStreams(4);
        soundPool = soundPoolBuilder.build();
        soundPool.load(this,R.raw.lights,1);
        soundPool.load(this,R.raw.shield_off,1);
        soundPool.load(this,R.raw.shield_on,1);
    }

    private int getVolume() {
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
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

    private static Fragment swipeFragment(int fragment) {
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
        if(mMicrophoneHandler.isMicOn())
        {
            mMicrophoneHandler.turnMicOff();
            view.setIcon(getResources().getDrawable(R.drawable.speaker_off_icon));
            view.invalidate();
        }
        else
        {
            mMicrophoneHandler.turnMicOn();
            view.setIcon(getResources().getDrawable(R.drawable.speaker_on_icon));
            view.invalidate();
        }
    }

    private void toggleSounds(MainButton view) {
        if(mAndroidBlue.isSoundOn())
        {
            mAndroidBlue.turnSoundOff();
            view.setIcon(getResources().getDrawable(R.drawable.music_off_icon));
            view.invalidate();
        }
        else
        {
            mAndroidBlue.turnSoundOn();
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
        toggleVoice((MainButton)view);
    }
    public void sounds(View view) {
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

    private static void updateTopBar(final TopBar mTopBar) {
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

    public static class MainFragment extends Fragment {
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

    public static class WarningsFragment extends Fragment {
        private ListView warningsList;
        private ArrayAdapter<Warning> warningsAdapter;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            mTopBar.setMenuName("Warnings");

            View view = inflater.inflate(R.layout.fragment_warnings, container, false);
            warningsList = (ListView) view.findViewById(R.id.warningslist);
            warningsAdapter = mAndroidBlue.getWarnings();
            warningsList.setAdapter(warningsAdapter);

            warningsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    currentFragment = warningsAdapter.getItem(position).getFragment();
                    mFragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_down, R.anim.slide_out_down)
                            .replace(R.id.container, swipeFragment(currentFragment))
                            .commit();
                }
            });
            return view;
        }
    }
}