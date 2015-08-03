package com.haloproject.bluetooth.InputHandlers;

import com.haloproject.bluetooth.AndroidBlue;
import com.haloproject.bluetooth.BluetoothInterfaces.JSONCommunicationDevice;

import org.json.JSONObject;

/**
 * Created by Tyler on 7/5/2015.
 */
public abstract class BeagleInputHandler {
    protected String location;
    protected JSONCommunicationDevice mCommunicationDevice;

    public BeagleInputHandler(String location, JSONCommunicationDevice communicationDevice) {
        this.location = location;
        mCommunicationDevice = communicationDevice;
    }

    protected void setState(String state) {
        try {
            JSONObject switchObject = new JSONObject();
            switchObject.put(location, state);
            mCommunicationDevice.getOutputStream().write(switchObject.toString().getBytes());
        } catch (Exception e) {

        }
    }

    protected boolean isStateSet(String state) {
        try {
            return mCommunicationDevice.getJSON().getString(location).equals(state);
        } catch (Exception e) {
            return false;
        }
    }
}
