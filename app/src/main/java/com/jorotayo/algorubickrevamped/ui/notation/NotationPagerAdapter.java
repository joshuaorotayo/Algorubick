package com.jorotayo.algorubickrevamped.ui.notation;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.jorotayo.algorubickrevamped.R;

public class NotationPagerAdapter extends FragmentStatePagerAdapter {
    private static final int[] TAB_TITLES = new int[]{R.string.tab_title_intro, R.string.tab_title_faces, R.string.tab_title_moves, R.string.tab_title_dbl_moves, R.string.tab_title_two_layer, R.string.tab_title_slices, R.string.tab_title_rotations, R.string.tab_title_algorithms};
    private final NotationFragment mContext;

    public NotationPagerAdapter(NotationFragment context, FragmentManager fm) {
        super(fm);
        this.mContext = context;
    }

    @NonNull
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                return new faces();
            case 2:
                return new moves();
            case 3:
                return new dbl_turns();
            case 4:
                return new two_layers();
            case 5:
                return new slices();
            case 6:
                return new rotations();
            case 7:
                return new algorithms();
            default:
                return new intro();
        }
    }

    public CharSequence getPageTitle(int position) {
        return this.mContext.getResources().getString(TAB_TITLES[position]);
    }

    public int getCount() {
        return 8;
    }
}
