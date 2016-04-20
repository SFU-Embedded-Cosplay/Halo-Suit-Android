package com.haloproject.projectspartanv2.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.haloproject.bluetooth.AndroidBlue;
import com.haloproject.projectspartanv2.FragmentSelector;
import com.haloproject.projectspartanv2.MainActivity;
import com.haloproject.projectspartanv2.MicrophoneHandler;
import com.haloproject.projectspartanv2.R;
import com.haloproject.projectspartanv2.view.MainButton;
import com.haloproject.projectspartanv2.view.TopBar;

import java.io.Serializable;

/**
 * Created by Tyler on 8/2/2015.
 */
public class MainFragment extends Fragment { //TODO: at some point in this classes lifecycle redraw the sound and voice buttons
    private LinearLayout mainMenu;
    private HorizontalScrollView scrollView;

    private AndroidBlue mAndroidBlue;
    private FragmentSelector mFragmentSelector;
    private TopBar mTopBar;

    private static final String ANDROID_BLUE_KEY = "androidBlue";
    private static final String FRAGMENT_SELECTOR_KEY = "fragmentSelector";

    public static MainFragment newInstance(AndroidBlue mAndroidBlue, FragmentSelector fragmentSelector) {
        MainFragment fragment = new MainFragment();

        final Bundle args = new Bundle();

        args.putSerializable(ANDROID_BLUE_KEY, mAndroidBlue);
        args.putSerializable(FRAGMENT_SELECTOR_KEY, (Serializable)  fragmentSelector);

        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAndroidBlue = (AndroidBlue) getArguments().getSerializable(ANDROID_BLUE_KEY);
        mFragmentSelector = (FragmentSelector) getArguments().getSerializable(FRAGMENT_SELECTOR_KEY);
        mTopBar = MainActivity.mTopBar;

        mFragmentSelector.setCurrentFragment(-1);

        mTopBar.setMenuName("Main Menu");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mainMenu = (LinearLayout) view.findViewById(R.id.mainmenu);
        scrollView = (HorizontalScrollView) view.findViewById(R.id.scrollview);

        MicrophoneHandler microphone = MicrophoneHandler.getInstance();
        if(!microphone.isMicOn()) {
            MainButton voiceButton = (MainButton) view.findViewById(R.id.menuButton9);
            voiceButton.setIcon(getResources().getDrawable(R.drawable.speaker_off_icon));
            voiceButton.invalidate();
        }

        if(!mAndroidBlue.isSoundOn()) {
            MainButton voiceButton = (MainButton) view.findViewById(R.id.menuButton8);
            voiceButton.setIcon(getResources().getDrawable(R.drawable.music_off_icon));
            voiceButton.invalidate();
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAndroidBlue.changeOnReceive();
    }
}
