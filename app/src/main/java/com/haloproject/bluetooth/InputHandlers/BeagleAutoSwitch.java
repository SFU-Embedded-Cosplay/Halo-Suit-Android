package com.haloproject.bluetooth.InputHandlers;

import com.haloproject.bluetooth.AndroidBlue;
import com.haloproject.bluetooth.BluetoothInterfaces.JSONCommunicationDevice;

/**
 * Created by Tyler on 7/5/2015.
 * turns things on or off or auto
 */
public class BeagleAutoSwitch extends BeagleInputHandler { //TODO: should probably extend BeagleSwitch (BeagleSwitch has on/off isOn/isOff functions)
    public BeagleAutoSwitch(String location, JSONCommunicationDevice communicationDevice) {
        super(location, communicationDevice);
    }

    public void auto() {
        setState("auto");
    }

    public void on() {
        setState("on");
    }

    public void off() {
        setState("off");
    }

    public boolean isAuto() {
        return isStateSet("auto");
    }

    public boolean isOn() {
        return isStateSet("on");
    }

    public boolean isOff() {
        return isStateSet("off");
    }
}
