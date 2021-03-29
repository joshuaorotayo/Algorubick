package com.jorotayo.algorubickrevamped.ui.notation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.jorotayo.algorubickrevamped.R;

public class NotationFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notation, container, false);
        ViewPager viewPager = view.findViewById(R.id.viewPager);
        viewPager.setAdapter(new NotationPagerAdapter(this, getChildFragmentManager()));
        ((TabLayout) view.findViewById(R.id.tab_layout)).setupWithViewPager(viewPager);
        return view;
    }
}
