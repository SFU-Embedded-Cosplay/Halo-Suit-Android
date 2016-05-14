package com.haloproject.projectspartanv2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.haloproject.bluetooth.AndroidBlue;
import com.haloproject.projectspartanv2.Fragments.MainFragment;
import com.haloproject.projectspartanv2.view.MainButton;
import com.haloproject.projectspartanv2.view.TopBar;

public class MainActivity extends ActionBarActivity {
    private FragmentManager mFragmentManager;
    private static AndroidBlue mAndroidBlue;

    private Animation onClickAnimation;

    private WindowManager.LayoutParams params;

    private static SharedPreferences mPreferences;
    private float x1, x2, y1, y2;
    //TODO: make mTopBar private and find a way so that all the fragments don't have to access it directly
    public static TopBar mTopBar; //cant pass this directly into a view with a bundle since it is not serializable or parcable

    private MicrophoneHandler mMicrophoneHandler;
    private SoundPool soundPool;

    private static FragmentSelector mFragmentSelector;

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupAudioServices();
        int volume = getVolume();

        mMicrophoneHandler = MicrophoneHandler.getInstance();

        AndroidBlue.setContext(getApplicationContext());
        mAndroidBlue = AndroidBlue.getInstance(soundPool, volume);

        setContentView(R.layout.activity_main);

        //set up wakelock
        params = new WindowManager.LayoutParams();
        params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        getWindow().setAttributes(params);
        mFragmentManager = getSupportFragmentManager();
        mFragmentSelector = new FragmentSelector(mAndroidBlue, mFragmentManager);

        if (savedInstanceState == null) {
            mFragmentManager.beginTransaction()
                    .add(R.id.container, MainFragment.newInstance(mAndroidBlue, mFragmentSelector))
                    .commit();

            mFragmentSelector.setCurrentFragment(-1);
        }





        mPreferences = getPreferences(MODE_PRIVATE);
        if (mPreferences.contains("bluetooth")) {
            String device = mPreferences.getString("bluetooth", "");
            if (mAndroidBlue.setBeagleBone(device)) {
                mAndroidBlue.connect();
            }
        }
        mTopBar = (TopBar) findViewById(R.id.topbar);
        updateTopBar();

        if (!mAndroidBlue.isEnabled()) {
            mAndroidBlue.enableBluetooth(this);
        }

        // Inform user that they entered testing mode
        if(mAndroidBlue.isTestingWithSocket()) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Warning")
                    .setMessage("Switching to socket testing mode since you device does not support Bluetooth." +
                            "\n\nCheck out the Halo-Suit-Testing-Server for more details.")
                    .setCancelable(false)
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    }).create().show();
        }

        //set on warning
        mAndroidBlue.setOnWarning(new Runnable() {
            @Override
            public void run() {
                mFragmentSelector.setCurrentFragment(5);
                mFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_down, R.anim.slide_out_down)
                        .replace(R.id.container, mFragmentSelector.getSelectedFragment())
                        .commit();
            }
        });

        onClickAnimation = AnimationUtils.loadAnimation(this, R.anim.button_onclick);
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
                    if (mFragmentSelector.getCurrentFragment() != -1 && mFragmentSelector.getCurrentFragment() > 0) {
                        mFragmentSelector.setCurrentFragment(mFragmentSelector.getCurrentFragment() - 1);

                        mFragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                                .replace(R.id.container, mFragmentSelector.getSelectedFragment())
                                .commit();
                    }
                } else if (x1 - x2 > 600) {
                    if (mFragmentSelector.getCurrentFragment() != -1 && mFragmentSelector.getCurrentFragment() < FragmentSelector.TOTAL_FRAGMENTS - 1) {
                        //left to right
                        mFragmentSelector.setCurrentFragment(mFragmentSelector.getCurrentFragment() + 1);
                        mFragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                                .replace(R.id.container, mFragmentSelector.getSelectedFragment())
                                .commit();
                    }
                } else if (y2 - y1 > 400) {
                    if (mFragmentSelector.getCurrentFragment() != -1) {
                        mFragmentSelector.setCurrentFragment(-1);
                        mFragmentManager.beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .replace(R.id.container, mFragmentSelector.getSelectedFragment())
                                .commit();
                    }
                }
                break;
        }
        return true;
    }

    private static void reDrawMainFragmentViewState() {

    }

    private void openCurrentFragment() {
        mFragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .replace(R.id.container, mFragmentSelector.getSelectedFragment())
                .commit();
    }

    private void toggleVoice(MainButton view) {
        if(mMicrophoneHandler.isMicOn()) {
            mMicrophoneHandler.turnMicOff();
            view.setIcon(getResources().getDrawable(R.drawable.speaker_off_icon));
            view.invalidate();
        } else {
            mMicrophoneHandler.turnMicOn();
            view.setIcon(getResources().getDrawable(R.drawable.speaker_on_icon));
            view.invalidate();
        }
    }

    private void toggleSounds(MainButton view) {
        if(mAndroidBlue.isSoundOn()) {
            mAndroidBlue.turnSoundOff();
            view.setIcon(getResources().getDrawable(R.drawable.music_off_icon));
            view.invalidate();
        }
        else {
            mAndroidBlue.turnSoundOn();
            view.setIcon(getResources().getDrawable(R.drawable.music_on_icon));
            view.invalidate();
        }
    }

    //TODO: do these need to accept a view?
    public void vitals(View view) {
        view.startAnimation(onClickAnimation);
        mFragmentSelector.setCurrentFragment(0);
        openCurrentFragment();
    }

    public void cooling(View view) {
        view.startAnimation(onClickAnimation);
        mFragmentSelector.setCurrentFragment(1);
        openCurrentFragment();
    }

    public void lighting(View view) {
        view.startAnimation(onClickAnimation);
        mFragmentSelector.setCurrentFragment(2);
        openCurrentFragment();
    }

    public void radar(View view) {
        view.startAnimation(onClickAnimation);
        mFragmentSelector.setCurrentFragment(3);
        openCurrentFragment();
    }

    public void batteries(View view) {
        view.startAnimation(onClickAnimation);
        mFragmentSelector.setCurrentFragment(4);
        openCurrentFragment();
    }

    public void warnings(View view) {
        view.startAnimation(onClickAnimation);
        mFragmentSelector.setCurrentFragment(5);
        openCurrentFragment();
    }

    public void settings(View view) {
        view.startAnimation(onClickAnimation);
        mFragmentSelector.setCurrentFragment(6);
        openCurrentFragment();
    }

    public void debug(View view) {
        view.startAnimation(onClickAnimation);
        mFragmentSelector.setCurrentFragment(7);
        openCurrentFragment();
    }

    public void voice(View view) {
        toggleVoice((MainButton) view);
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

    private static void updateTopBar() {
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


}