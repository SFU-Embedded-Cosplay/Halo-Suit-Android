package com.haloproject.bluetooth.OutputHandlers;

import com.haloproject.bluetooth.AndroidBlue;
import com.haloproject.bluetooth.BluetoothInterfaces.JSONCommunicationDevice;

/**
 * Created by Tyler on 7/5/2015.
 */
public class BeagleIntegerOutput {

    private String location;
    private JSONCommunicationDevice mCommunicationDevice;

    public BeagleIntegerOutput(String location, JSONCommunicationDevice communicationDevice) {
        this.location = location;
        mCommunicationDevice = communicationDevice;
    }

    public int getValue() {
        try {
            return mCommunicationDevice.getJSON().getInt(location); //get double could accept and integer because double is bigger than an integer. thus this code will work for both ints and doubles
        } catch (Exception e) {
            return -1;
        }
    }
}
