package com.haloproject.bluetooth.OutputHandlers;

import com.haloproject.bluetooth.AndroidBlue;

import org.json.JSONObject;

/**
 * Created by Tyler on 7/3/2015.
 */
public class BeagleDoubleOutput {
    private String location;
    private AndroidBlue mAndroidBlue;

    public BeagleDoubleOutput(String location) {
        this.location = location;
        mAndroidBlue = AndroidBlue.getInstance();
    }

    public double getValue() {
        try {
            return mAndroidBlue.getJSON().getDouble(location); //get double could accept and integer because double is bigger than an integer. thus this code will work for both ints and doubles
        } catch (Exception e) {
            return -1000.0;
        }
    }
}