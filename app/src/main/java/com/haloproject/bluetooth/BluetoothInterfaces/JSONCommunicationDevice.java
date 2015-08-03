package com.haloproject.bluetooth.BluetoothInterfaces;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Tyler on 7/25/2015.
 */
public interface JSONCommunicationDevice {

    JSONObject getJSON();
    OutputStream getOutputStream() throws IOException;
}
