package com.haloproject.bluetooth.InputHandlers;

import com.haloproject.bluetooth.AndroidBlue;
import com.haloproject.bluetooth.BluetoothInterfaces.JSONCommunicationDevice;

/**
 * Created by Tyler on 7/5/2015.
 * used for turning things on or off on the beaglebone
 */
public class BeagleSwitch extends BeagleInputHandler {
    public BeagleSwitch(String location, JSONCommunicationDevice communicationDevice) {
        super(location, communicationDevice);
    }

    public void on() {
        setState("on");
    }

    public void off() {
        setState("off");
    }

    public boolean isOn() {
        return isStateSet("on");
    }

    public boolean isOff() {
        return isStateSet("off");
    }
}
