package com.haloproject.projectspartanv2;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
import android.widget.Toast;

import com.haloproject.bluetooth.AndroidBlue;

public class MainActivity extends ActionBarActivity implements SensorEventListener {
    static private FragmentManager mFragmentManager;
    static private AndroidBlue mAndroidBlue;
    final int TOTAL_SWIPE_FRAGMENTS = 7;
    private boolean isUpRight = true;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    static private int currentFragment; //-1 means its at main menu
    static private SharedPreferences mPreferences;
    private float x1, x2, y1, y2;
    static private TopBar mTopBar;

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == mSensor) {
            final float z = event.values[2];
            Toast.makeText(getApplicationContext(), String.format("%f", z), Toast.LENGTH_SHORT).show();
        }
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
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL, 1000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mFragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            mFragmentManager.beginTransaction()
                    .add(R.id.container, new MainFragment())
                    .commit();
        }
        AndroidBlue.setContext(getApplicationContext());
        mAndroidBlue = AndroidBlue.getInstance();
        mPreferences = getPreferences(MODE_PRIVATE);
        if (mPreferences.contains("bluetooth")) {
            String device = mPreferences.getString("bluetooth", "");
            if (mAndroidBlue.setBeagleBone(device)) {
                mAndroidBlue.connect();
            }
        }
        currentFragment = -1;
        mTopBar = (TopBar) findViewById(R.id.topbar);
        updateTopBar(mTopBar);

        //create sensor
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
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

    private void toggleVoice() {
        mFragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .replace(R.id.container, swipeFragment(currentFragment))
                .commit();
    }

    private void toggleSounds() {
        mFragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .replace(R.id.container, swipeFragment(currentFragment))
                .commit();
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
        toggleVoice();
    }
    public void sounds(View view) {
        currentFragment = 8;
        toggleSounds();
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

        mAndroidBlue.setOnDisconnect(new Runnable() {
            @Override
            public void run() {
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
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            mTopBar.setMenuName("Cooling");
            View view = inflater.inflate(R.layout.fragment_cooling, container, false);
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
        private TempWheel waterTemp;
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            mTopBar.setMenuName("Vitals");
            View view = inflater.inflate(R.layout.fragment_vitals, container, false);
            headTemp = (TempWheel) view.findViewById(R.id.headTemp);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        double oldTemp = headTemp.getTemp();
                        double newTemp = (oldTemp + 0.2);
                        headTemp.setTemp(newTemp);
                        try {
                            Thread.sleep(300);
                        } catch (Exception e) {

                        }
                    }
                }
            }).start();

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
}