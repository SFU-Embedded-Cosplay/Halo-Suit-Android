package org.unsc.projectspartan;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import java.io.File;
import java.util.Set;


public class LoadActivity extends Activity {
    private BluetoothAdapter mAdapter;
    private Set<BluetoothDevice> mPairedDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mAdapter.enable();
        mPairedDevices = mAdapter.getBondedDevices();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (BluetoothDevice mDevice : mPairedDevices) {
                    if (mDevice.getName().contains("beagle")) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra(BluetoothDevice.EXTRA_DEVICE, mDevice);
                        startActivity(intent);
                    }
                }
            }
        }, 1000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.load, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
