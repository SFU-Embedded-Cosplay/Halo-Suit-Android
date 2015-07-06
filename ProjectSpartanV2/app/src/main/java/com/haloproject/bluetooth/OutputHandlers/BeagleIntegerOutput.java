package com.haloproject.bluetooth.OutputHandlers;

import com.haloproject.bluetooth.AndroidBlue;

/**
 * Created by Tyler on 7/5/2015.
 */
public class BeagleIntegerOutput {

    private String location;
    private AndroidBlue mAndroidBlue;

    public BeagleIntegerOutput(String location) {
        this.location = location;
        mAndroidBlue = AndroidBlue.getInstance();
    }

    public int getValue() {
        try {
            return mAndroidBlue.getJSON().getInt(location); //get double could accept and integer because double is bigger than an integer. thus this code will work for both ints and doubles
        } catch (Exception e) {
            return -1;
        }
    }
}
