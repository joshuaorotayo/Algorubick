package com.jorotayo.algorubickrevamped.ui.notation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.jorotayo.algorubickrevamped.R;

public class NotationFragment extends Fragment implements MenuProvider {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notation, container, false);
        ViewPager viewPager = view.findViewById(R.id.viewPager);
        viewPager.setAdapter(new NotationPagerAdapter(this, getChildFragmentManager()));
        ((TabLayout) view.findViewById(R.id.tab_layout)).setupWithViewPager(viewPager);

        requireActivity().addMenuProvider(this);
        return view;
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        MenuItem actionBarSearch = menu.findItem(R.id.actionbar_search);
        MenuItem actionBarStatistics = menu.findItem(R.id.actionbar_statistics);
        if (actionBarSearch != null){
            menu.findItem(R.id.actionbar_search).setVisible(false);
        }
        if( actionBarStatistics != null){
            menu.findItem(R.id.actionbar_statistics).setVisible(false);
        }
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }
}
