package com.haloproject.bluetooth.InputHandlers;

import com.haloproject.bluetooth.AndroidBlue;

import org.json.JSONObject;

/**
 * Created by Tyler on 7/5/2015.
 */
public abstract class BeagleInputHandler {
    protected String location;
    protected AndroidBlue androidBlue;

    public BeagleInputHandler(String location) {
        this.location = location;
        this.androidBlue = AndroidBlue.getInstance();
    }

    protected void setState(String state) {
        try {
            JSONObject switchObject = new JSONObject();
            switchObject.put(location, state);
            androidBlue.getOutputStream().write(switchObject.toString().getBytes());
        } catch (Exception e) {

        }
    }

    protected boolean isStateSet(String state) {
        try {
            return androidBlue.getJSON().getString(location).equals(state);
        } catch (Exception e) {
            return false;
        }
    }
}
