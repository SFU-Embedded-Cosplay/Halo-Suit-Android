package com.haloproject.projectspartanv2;

import android.support.annotation.Nullable;
import android.app.FragmentManager;
import android.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.haloproject.projectspartanv2.AndroidBlue;

public class MainActivity extends ActionBarActivity {
    static private FragmentManager mFragmentManager;
    static private AndroidBlue mAndroidBlue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentManager = getFragmentManager();
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
                mFragmentManager.beginTransaction()
                        .add(R.id.container, new MainFragment())
                        .commit();
        }
        AndroidBlue.setContext(getApplicationContext());
        AndroidBlue.setActivity(this);
        mAndroidBlue = AndroidBlue.getInstance();
    }

    public void tempCool(View view) {
        mFragmentManager.beginTransaction()
                .replace(R.id.container, new CoolingFragment())
                .addToBackStack("test").commit();
    }

    public void settings(View view) {
        mFragmentManager.beginTransaction()
                .replace(R.id.container, new SettingsFragment())
                .addToBackStack("test").commit();
    }

    public void lighting(View view) {
        mFragmentManager.beginTransaction()
                .replace(R.id.container, new LightingFragment())
                .addToBackStack("test").commit();
    }

    public void radar(View view) {
        mFragmentManager.beginTransaction()
                .replace(R.id.container, new RadarFragment())
                .addToBackStack("test").commit();
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

    static public class LightingFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_lighting, container, false);
        }
    }

    static public class RadarFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_radar, container, false);
        }
    }


    static public class MainFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_main, container, false);
        }
    }

    static public class CoolingFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            final View view = inflater.inflate(R.layout.fragment_cooling, container, false);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView headtemp = (TextView) view.findViewById(R.id.headtemp);
                                Log.d("Temp", Double.toString(mAndroidBlue.headTemperature.getValue()));
                                headtemp.setText(String.format("%.2f", mAndroidBlue.headTemperature.getValue()));
                            }
                        });
                        try {
                            Thread.sleep(4000);
                        } catch (Exception e) {

                        }
                    }
                }
            }).start();
            return view;
        }
    }

    static public class SettingsFragment extends Fragment {
        private ListView btdevices;
        //private ArrayAdapter<BluetoothDevice> mArrayAdapter;
        private Switch switch1;
        private Button discover;
        private Button configure;
        private View view;
        private RadioButton connected;

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            view = inflater.inflate(R.layout.fragment_settings, container, false);
            switch1 = (Switch) view.findViewById(R.id.switch1);
            switch1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean on = ((Switch)v).isChecked();
                    if (on) {
                        mAndroidBlue.enableBluetooth();
                    } else {
                        mAndroidBlue.disableBluetooth();
                    }
                }
            });
            btdevices = (ListView) view.findViewById(R.id.btdevices);
            btdevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    RadioGroup group = ((RadioGroup) view.findViewById(R.id.radioGroup));
                    RadioButton bb   = ((RadioButton) group.findViewById(R.id.setbeaglebone));
                    RadioButton gg   = ((RadioButton) group.findViewById(R.id.setgoogleglass));
                    if (bb.isChecked()) {
                        mAndroidBlue.setBeagleBone(position);
                        ((TextView)view.findViewById(R.id.beaglebone)).setText(mAndroidBlue.getBeagleBone().getAddress());
                        if (mAndroidBlue.setDevice(position)) {
                            mAndroidBlue.connect();
                        }
                    } else if (gg.isChecked()) {
                        mAndroidBlue.setGoogleGlass(position);
                        ((TextView)view.findViewById(R.id.googleglass)).setText(mAndroidBlue.getGoogleGlass().getAddress());
                    }
                }
            });
            discover = (Button) view.findViewById(R.id.discover);
            discover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btdevices.setAdapter(mAndroidBlue.getDeviceStrings());
                    mAndroidBlue.startDiscovery();
                }
            });
            connected = (RadioButton) view.findViewById(R.id.connected);
            if (mAndroidBlue.isConnected()) {
                connected.setChecked(true);
            } else {
                connected.setChecked(false);
            }
            connected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (mAndroidBlue.isConnected()) {
                        ((RadioButton) buttonView).setChecked(true);
                    } else {
                        ((RadioButton) buttonView).setChecked(false);
                    }
                }
            });
            mAndroidBlue.setOnConnect(new Runnable() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Connected", Toast.LENGTH_LONG).show();
                            connected.setChecked(true);
                        }
                    });
                }
            });
            configure = (Button) view.findViewById(R.id.configure);
            configure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAndroidBlue.sendConfiguration();
                }
            });
            return view;
        }

        @Override
        public void onStart() {
            super.onStart();
            if (mAndroidBlue.isEnabled()) {
                switch1.setChecked(true);
            } else {
                switch1.setChecked(false);
            }
        }
    }


}
