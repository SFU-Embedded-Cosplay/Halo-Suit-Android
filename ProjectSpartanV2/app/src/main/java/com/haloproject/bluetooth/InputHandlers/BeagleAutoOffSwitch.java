package com.haloproject.bluetooth.InputHandlers;

/**
 * Created by Tyler on 7/5/2015.
 * turn switched auto or off
 */
public class BeagleAutoOffSwitch extends BeagleInputHandler {
    public BeagleAutoOffSwitch(String location) {
        super(location);
    }

    public void auto() {
        setState("auto");
    }

    public void off() {
        setState("off");
    }

    public boolean isAuto() {
        return isStateSet("auto");
    }

    public boolean isOff() {
        return isStateSet("off");
    }
}
