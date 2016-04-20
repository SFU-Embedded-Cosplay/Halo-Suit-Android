package com.haloproject.projectspartanv2.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.haloproject.bluetooth.AndroidBlue;
import com.haloproject.projectspartanv2.FragmentSelector;
import com.haloproject.projectspartanv2.MainActivity;
import com.haloproject.projectspartanv2.R;
import com.haloproject.projectspartanv2.Warning;
import com.haloproject.projectspartanv2.view.TopBar;

import java.io.Serializable;

/**
 * Created by Tyler on 4/19/2016.
 */
public class WarningsFragment extends Fragment {
    private ListView warningsList;
    private ArrayAdapter<Warning> warningsAdapter;

    private TopBar mTopBar;
    private AndroidBlue mAndroidBlue;
    private FragmentManager mFragmentManager;
    private FragmentSelector mFragmentSelector;

    private static final String ANDROID_BLUE_KEY = "androidBlue";
    private static final String FRAGMENT_MANAGER_KEY = "fragmentManager";
    private static final String FRAGMENT_SELECTOR_KEY = "fragmentSelector";

    public static WarningsFragment newInstance(AndroidBlue androidBlue, FragmentManager fragmentManager, FragmentSelector fragmentSelector) {
        WarningsFragment fragment = new WarningsFragment();

        final Bundle args = new Bundle();

        args.putSerializable(ANDROID_BLUE_KEY, androidBlue);
        args.putSerializable(FRAGMENT_MANAGER_KEY, (Serializable) fragmentManager);
        args.putSerializable(FRAGMENT_SELECTOR_KEY, (Serializable) fragmentSelector);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAndroidBlue = (AndroidBlue) getArguments().getSerializable(ANDROID_BLUE_KEY);
        mFragmentManager = (FragmentManager) getArguments().getSerializable(FRAGMENT_MANAGER_KEY);
        mFragmentSelector = (FragmentSelector) getArguments().getSerializable(FRAGMENT_SELECTOR_KEY);
        mTopBar = MainActivity.mTopBar;

        mTopBar.setMenuName("Warnings");

        View view = inflater.inflate(R.layout.fragment_warnings, container, false);
        warningsList = (ListView) view.findViewById(R.id.warningslist);
        warningsAdapter = mAndroidBlue.getWarnings();
        warningsList.setAdapter(warningsAdapter);

        warningsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int currentFragment = warningsAdapter.getItem(position).getFragment();
                mFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_down, R.anim.slide_out_down)
                        .replace(R.id.container, mFragmentSelector.getSelectedFragment())
                        .commit();
            }
        });
        return view;
    }
}
