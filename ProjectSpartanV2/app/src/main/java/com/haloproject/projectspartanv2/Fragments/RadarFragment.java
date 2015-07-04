package com.haloproject.projectspartanv2.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haloproject.projectspartanv2.MainActivity;
import com.haloproject.projectspartanv2.R;
import com.haloproject.projectspartanv2.view.TopBar;

/**
 * Created by Tyler on 6/29/2015.
 */
public class RadarFragment extends Fragment {
    private TopBar mTopBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mTopBar = MainActivity.mTopBar;

        // Inflate the layout for this fragment
        mTopBar.setMenuName("Radar");
        View view = inflater.inflate(R.layout.fragment_radar, container, false);
        return view;
    }
}
