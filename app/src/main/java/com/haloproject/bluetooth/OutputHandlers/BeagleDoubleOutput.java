package com.haloproject.bluetooth.OutputHandlers;

import com.haloproject.bluetooth.AndroidBlue;
import com.haloproject.bluetooth.BluetoothInterfaces.JSONCommunicationDevice;

import org.json.JSONObject;

/**
 * Created by Tyler on 7/3/2015.
 */
public class BeagleDoubleOutput {
    private String location;
    private JSONCommunicationDevice mCommunicationDevice;

    public BeagleDoubleOutput(String location, JSONCommunicationDevice communicationDevice) {
        this.location = location;
        mCommunicationDevice = communicationDevice;
    }

    public double getValue() {
        try {
            return mCommunicationDevice.getJSON().getDouble(location); //get double could accept and integer because double is bigger than an integer. thus this code will work for both ints and doubles
        } catch (Exception e) {
            return -1000.0;
        }
    }
}