import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.ArrayAdapter;

import java.util.Set;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

/**
 * Created by Adam Brykajlo on 18/02/15.
 */
public class AndroidBlue {
    private BluetoothSocket mSocket;
    private ArrayAdapter<BluetoothDevice> mArrayAdapter;

    public AndroidBlue() {
        Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            if (device.getName().contains("beagle")) {

            }
        }
    }


}
