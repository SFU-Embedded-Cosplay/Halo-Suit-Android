package com.haloproject.projectspartanv2;

/**
 * Created by adam on 12/04/15.
 */
public enum Warning {
    CRIT_HIGH_BODY_TEMP("critical high body temperature", 0),
    HIGH_BODY_TEMP("high body temperature", 0),
    CRIT_LOW_BODY_TEMP("critical low body temperature", 0),
    LOW_BODY_TEMP("low body temperature", 0),
    CRIT_HIGH_HEAD_TEMP("critical high head temperature", 0),
    HIGH_HEAD_TEMP("high head temperature", 0),
    CRIT_LOW_HEAD_TEMP("critical low head temperature", 0),
    LOW_HEAD_TEMP("low head temperature", 0),
    HIGH_WATER_TEMP("high water temperature", 1),
    LOW_WATER_TEMP("low water temperature", 1),
    LOW_WATER_FLOW("low water flow", 1),
    HIGH_AMP_BATTERY_LOW("low 8AH battery warning", 4),
    LOW_AMP_BATTERY_LOW("low 2AH battery warning", 4),
    HUD_BATTERY_LOW("low hud battery warning", 4),
    PHONE_BATTERY_LOW("low phone battery warning", 4);





    private Warning(String warning, int fragment) {
        this.warning = warning;
        this.isSet = false;
        this.fragment = fragment;
    }

    private String warning;
    private int fragment;
    private boolean isSet;

    public boolean equals(String warning) {
        return warning.equals(this.warning);
    }

    @Override
    public String toString() {
        return warning;
    }

    public int getFragment() {
        return fragment;
    }

    public boolean isSet() {
        return isSet;
    }

    public void set(boolean isSet) {
        this.isSet = isSet;
    }
}
