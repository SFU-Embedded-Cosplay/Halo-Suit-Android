package com.haloproject.bluetooth.InputHandlers;

/**
 * Created by Tyler on 7/5/2015.
 * turns things on or off or auto
 */
public class BeagleAutoSwitch extends BeagleInputHandler {
    public BeagleAutoSwitch(String location) {
        super(location);
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
