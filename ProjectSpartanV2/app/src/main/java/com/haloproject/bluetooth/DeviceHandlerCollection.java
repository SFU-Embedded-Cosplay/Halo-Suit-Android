package com.haloproject.bluetooth;

import android.bluetooth.BluetoothClass;

import com.haloproject.bluetooth.InputHandlers.BeagleAutoOffSwitch;
import com.haloproject.bluetooth.InputHandlers.BeagleAutoSwitch;
import com.haloproject.bluetooth.InputHandlers.BeagleSwitch;
import com.haloproject.bluetooth.OutputHandlers.BeagleDoubleOutput;
import com.haloproject.bluetooth.OutputHandlers.BeagleIntegerOutput;

/**
 * Created by Tyler on 7/6/2015.
 */
public class DeviceHandlerCollection {

    private static DeviceHandlerCollection deviceHandlerCollection;

    //batteries
    public final BeagleIntegerOutput battery8AH;
    public final BeagleIntegerOutput battery2AH;
    public final BeagleIntegerOutput batteryAndroid;
    public final BeagleIntegerOutput batteryGlass;

    //lights
    public final BeagleAutoSwitch mainLights;
    public final BeagleSwitch redHeadLight;
    public final BeagleSwitch whiteHeadLight;

    //cooling
    //input
    public final BeagleAutoOffSwitch peltier;
    public final BeagleAutoOffSwitch waterPump;
    public final BeagleSwitch headFans;
    //output
    public final BeagleIntegerOutput flowRate;
    public final BeagleDoubleOutput waterTemperature;

    //vitals
    public final BeagleDoubleOutput headTemperature;
    public final BeagleDoubleOutput crotchTemperature;
    public final BeagleDoubleOutput armpitsTemperature;
    public final BeagleIntegerOutput heartRate;

    private DeviceHandlerCollection() {
        headTemperature = new BeagleDoubleOutput("head temperature");
        crotchTemperature = new BeagleDoubleOutput("crotch temperature");
        armpitsTemperature = new BeagleDoubleOutput("armpits temperature");
        waterTemperature = new BeagleDoubleOutput("water temperature");

        flowRate = new BeagleIntegerOutput("flow rate");
        heartRate = new BeagleIntegerOutput("heart rate");
        battery2AH = new BeagleIntegerOutput("2 AH battery");
        battery8AH = new BeagleIntegerOutput("8 AH battery");
        batteryAndroid = new BeagleIntegerOutput("phone battery");
        batteryGlass = new BeagleIntegerOutput("hud battery");


        redHeadLight = new BeagleSwitch("head lights red");
        whiteHeadLight = new BeagleSwitch("head lights white");
        peltier = new BeagleAutoOffSwitch("peltier");
        waterPump = new BeagleAutoOffSwitch("water pump");
        headFans = new BeagleSwitch("head fans");
        mainLights = new BeagleAutoSwitch("lights");
    }

    public static DeviceHandlerCollection getInstance() {
        if(deviceHandlerCollection == null) {
            deviceHandlerCollection = new DeviceHandlerCollection();
        }

        return deviceHandlerCollection;
    }

}
