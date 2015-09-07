package com.haloproject.projectspartanv2.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.haloproject.bluetooth.AndroidBlue;
import com.haloproject.projectspartanv2.MainActivity;
import com.haloproject.projectspartanv2.R;
import com.haloproject.projectspartanv2.view.TopBar;

/**
 * Created by Tyler on 8/2/2015.
 */
public class MainFragment extends Fragment {
    private LinearLayout mainMenu;
    private HorizontalScrollView scrollView;

    private AndroidBlue mAndroidBlue;
    private TopBar mTopBar;

    private static final String ANDROID_BLUE_KEY = "androidBlue";

    public static MainFragment newInstance(AndroidBlue mAndroidBlue) {
        MainFragment fragment = new MainFragment();

        final Bundle args = new Bundle();

        args.putSerializable(ANDROID_BLUE_KEY, mAndroidBlue);

        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAndroidBlue = (AndroidBlue) getArguments().getSerializable(ANDROID_BLUE_KEY);
        mTopBar = MainActivity.mTopBar;

        MainActivity.setCurrentFragmentToMainMenu();

        mTopBar.setMenuName("Main Menu");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mainMenu = (LinearLayout) view.findViewById(R.id.mainmenu);
        scrollView = (HorizontalScrollView) view.findViewById(R.id.scrollview);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAndroidBlue.changeOnReceive();
    }
}
