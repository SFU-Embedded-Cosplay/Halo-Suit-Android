package com.haloproject.projectspartanv2;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.haloproject.bluetooth.AndroidBlue;
import com.haloproject.bluetooth.DeviceHandlerCollection;
import com.haloproject.projectspartanv2.Fragments.BatteryFragment;
import com.haloproject.projectspartanv2.Fragments.CoolingFragment;
import com.haloproject.projectspartanv2.Fragments.DebugFragment;
import com.haloproject.projectspartanv2.Fragments.GunFragment;
import com.haloproject.projectspartanv2.Fragments.LightingFragment;
import com.haloproject.projectspartanv2.Fragments.MainFragment;
import com.haloproject.projectspartanv2.Fragments.RadarFragment;
import com.haloproject.projectspartanv2.Fragments.SettingsFragment;
import com.haloproject.projectspartanv2.Fragments.VitalsFragment;
import com.haloproject.projectspartanv2.Fragments.WarningsFragment;

import java.io.Serializable;

/**
 * Created by Tyler on 4/19/2016.
 */
public class FragmentSelector implements Serializable {
    public static final int TOTAL_FRAGMENTS = 8;

    private int currentFragment; //-1 means its at main menu

    private AndroidBlue mAndroidBlue;
    private FragmentManager mFragmentManager;

    public FragmentSelector(AndroidBlue androidBlue, FragmentManager fragmentManager) {
        this.mAndroidBlue = androidBlue;
        this.mFragmentManager = fragmentManager;
    }

    public void setCurrentFragment(int newCurrentFragment) {
        currentFragment = newCurrentFragment;
    }

    public int getCurrentFragment() {
        return currentFragment;
    }

    public Fragment getSelectedFragment() {
        switch (currentFragment) {
            case 0:
                return VitalsFragment.newInstance(mAndroidBlue, DeviceHandlerCollection.getInstance(mAndroidBlue));
            case 1:
                return CoolingFragment.newInstance(mAndroidBlue, DeviceHandlerCollection.getInstance(mAndroidBlue));
            case 2:
                return LightingFragment.newInstance(mAndroidBlue , DeviceHandlerCollection.getInstance(mAndroidBlue));
            case 3:
                return GunFragment.newInstance(DeviceHandlerCollection.getInstance(mAndroidBlue));
            case 4:
                return RadarFragment.newInstance();
            case 5:
                return BatteryFragment.newInstance(mAndroidBlue, DeviceHandlerCollection.getInstance(mAndroidBlue));
            case 6:
                return WarningsFragment.newInstance(mAndroidBlue, mFragmentManager, this);
            case 7:
                return SettingsFragment.newInstance(mAndroidBlue);
            case 8:
                return DebugFragment.newInstance(mAndroidBlue, DeviceHandlerCollection.getInstance(mAndroidBlue));
            default:
                return MainFragment.newInstance(mAndroidBlue, this);
        }
    }
}
