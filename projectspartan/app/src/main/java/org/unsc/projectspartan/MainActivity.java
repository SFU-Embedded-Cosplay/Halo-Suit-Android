package org.unsc.projectspartan;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;


public class MainActivity extends Activity {
    private BluetoothDevice mDevice;
    private BluetoothSocket mSocket;
    private TextView tv0, tv1, tv2, tv3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        tv0 = (TextView) findViewById(R.id.head_temp);
        tv1 = (TextView) findViewById(R.id.armpits_temp);
        tv2 = (TextView) findViewById(R.id.crotch_temp);
        tv3 = (TextView) findViewById(R.id.water_temp);

        mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        new connectToHost().start();
    }

    private class connectToHost extends Thread {
        @Override
        public void run() {
            try {
                Method m = mDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                Log.d("Device", mDevice.getName() + " " + mDevice.getAddress());
                mSocket = (BluetoothSocket) m.invoke(mDevice, Integer.valueOf(3));
//                mSocket = mDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString("00000000-0000-0000-0000-00000000ABCD"));
                mSocket.connect();
                Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
                new receivingThread().start();
            } catch (Exception e) {
                Log.e("", e.getLocalizedMessage());
                //Toast.makeText(getApplicationContext(), "Did Not Connect", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class receivingThread extends Thread {
        private byte[] bytes;
        private double temp0, temp1, temp2, temp3;
        @Override
        public void run() {
            while (true) {
                try {

                    bytes = new byte[1024];
                    int i = mSocket.getInputStream().read(bytes);
                    JSONObject j = new JSONObject(new String(bytes));
                    temp0 = j.getDouble("head temperature");
                    temp1 = j.getDouble("armpits temperature");
                    temp2 = j.getDouble("crotch temperature");
                    temp3 = j.getDouble("water temperature");
                    if (i != -1) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv0.setText(String.format("Head    %4.1f C", temp0));
                                tv1.setText(String.format("Armpits %4.1f C", temp1));
                                tv2.setText(String.format("Crotch  %4.1f C", temp2));
                                tv3.setText(String.format("Water   %4.1f C", temp3));
                            }
                        });
                    }

                } catch (Exception e) {

                }

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    public void lightsOn(View view) {
        try {
            JSONObject j = new JSONObject();
            j.put("lights", "on");
            mSocket.getOutputStream().write(j.toString().getBytes());
        } catch (Exception e) {

        }
    }

    public void lightsOff(View view) {
        try {
            JSONObject j = new JSONObject();
            j.put("lights", "off");
            mSocket.getOutputStream().write(j.toString().getBytes());
        } catch (Exception e) {

        }
    }

    public void lightsAuto(View view) {
        try {
            JSONObject j = new JSONObject();
            j.put("lights", "auto");
            mSocket.getOutputStream().write(j.toString().getBytes());
        } catch (Exception e) {

        }
    }

    public void whiteHeadlightsOn(View view) {
        try {
            JSONObject j = new JSONObject();
            j.put("head lights white", "on");
            mSocket.getOutputStream().write(j.toString().getBytes());
        } catch (Exception e) {

        }
    }

    public void whiteHeadlightsOff(View view) {
        try {
            JSONObject j = new JSONObject();
            j.put("head lights white", "off");
            mSocket.getOutputStream().write(j.toString().getBytes());
        } catch (Exception e) {

        }
    }

    public void redHeadlightsOn(View view) {
        try {
            JSONObject j = new JSONObject();
            j.put("head lights red", "on");
            mSocket.getOutputStream().write(j.toString().getBytes());
        } catch (Exception e) {

        }
    }

    public void redHeadlightsOff(View view) {
        try {
            JSONObject j = new JSONObject();
            j.put("head lights red", "off");
            mSocket.getOutputStream().write(j.toString().getBytes());
        } catch (Exception e) {

        }
    }

    public void headFanOn(View view) {
        try {
            JSONObject j = new JSONObject();
            j.put("head fans", "on");
            mSocket.getOutputStream().write(j.toString().getBytes());
        } catch (Exception e) {

        }
    }

    public void headFanOff(View view) {
        try {
            JSONObject j = new JSONObject();
            j.put("head fans", "off");
            mSocket.getOutputStream().write(j.toString().getBytes());
        } catch (Exception e) {

        }
    }

    public void pumpOn(View view) {
        try {
            JSONObject j = new JSONObject();
            j.put("water pump", "on");
            j.put("water fan", "on");
            mSocket.getOutputStream().write(j.toString().getBytes());
        } catch (Exception e) {

        }
    }

    public void pumpOff(View view) {
        try {
            JSONObject j = new JSONObject();
            j.put("water pump", "off");
            j.put("water fan", "off");
            mSocket.getOutputStream().write(j.toString().getBytes());
        } catch (Exception e) {

        }
    }

    public void peltierOn(View view) {
        try {
            JSONObject j = new JSONObject();
            j.put("peltier", "on");
            mSocket.getOutputStream().write(j.toString().getBytes());
        } catch (Exception e) {

        }
    }

    public void peltierOff(View view) {
        try {
            JSONObject j = new JSONObject();
            j.put("peltier", "off");
            mSocket.getOutputStream().write(j.toString().getBytes());
        } catch (Exception e) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mSocket.close();
        } catch (Exception e) {

        }
    }
}
