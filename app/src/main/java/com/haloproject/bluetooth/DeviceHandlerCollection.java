package com.haloproject.bluetooth;

import android.bluetooth.BluetoothClass;

import com.haloproject.bluetooth.BluetoothInterfaces.JSONCommunicationDevice;
import com.haloproject.bluetooth.InputHandlers.BeagleAutoOffSwitch;
import com.haloproject.bluetooth.InputHandlers.BeagleAutoSwitch;
import com.haloproject.bluetooth.InputHandlers.BeagleSwitch;
import com.haloproject.bluetooth.OutputHandlers.BeagleDoubleOutput;
import com.haloproject.bluetooth.OutputHandlers.BeagleIntegerOutput;

import java.io.Serializable;

/**
 * Created by Tyler on 7/6/2015.
 */
public class DeviceHandlerCollection implements Serializable {

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

    private DeviceHandlerCollection(JSONCommunicationDevice communicationDevice) {
        headTemperature = new BeagleDoubleOutput("head temperature", communicationDevice);
        crotchTemperature = new BeagleDoubleOutput("crotch temperature", communicationDevice);
        armpitsTemperature = new BeagleDoubleOutput("armpits temperature", communicationDevice);
        waterTemperature = new BeagleDoubleOutput("water temperature", communicationDevice);

        flowRate = new BeagleIntegerOutput("flow rate", communicationDevice);
        heartRate = new BeagleIntegerOutput("heart rate", communicationDevice);
        battery2AH = new BeagleIntegerOutput("2 AH battery", communicationDevice);
        battery8AH = new BeagleIntegerOutput("8 AH battery", communicationDevice);
        batteryAndroid = new BeagleIntegerOutput("phone battery", communicationDevice);
        batteryGlass = new BeagleIntegerOutput("hud battery", communicationDevice);


        redHeadLight = new BeagleSwitch("head lights red", communicationDevice);
        whiteHeadLight = new BeagleSwitch("head lights white", communicationDevice);
        peltier = new BeagleAutoOffSwitch("peltier", communicationDevice);
        waterPump = new BeagleAutoOffSwitch("water pump", communicationDevice);
        headFans = new BeagleSwitch("head fans", communicationDevice);
        mainLights = new BeagleAutoSwitch("lights", communicationDevice);
    }

    public static DeviceHandlerCollection getInstance(JSONCommunicationDevice communicationDevice) {
        if(deviceHandlerCollection == null) {
            deviceHandlerCollection = new DeviceHandlerCollection(communicationDevice);
        }

        return deviceHandlerCollection;
    }

}
