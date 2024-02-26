package com.example.fellowtraveler;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.fellowtraveler.Fragments.StatisticsFragment;
import com.example.fellowtraveler.Fragments.SpeedGraphFragment;
import com.example.fellowtraveler.Fragments.ElevationGraphFragment;

public class MyPagerAdapter extends FragmentStateAdapter {
    int mNumOfTabs;
    String trackname;
    public StatisticsFragment tab1;
    public SpeedGraphFragment speedFragment;
    public ElevationGraphFragment elevationFragment;

    public MyPagerAdapter(FragmentActivity context, int NumOfTabs, String trackname) {
        super(context);
        this.mNumOfTabs = NumOfTabs;
        this.trackname = trackname;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Bundle args;
        switch (position) {
            case 0:
                tab1 = new StatisticsFragment();
                return tab1;
            case 1:
                speedFragment = new SpeedGraphFragment();
                args = new Bundle();
                args.putString("track",trackname);
                speedFragment.setArguments(args);
                return speedFragment;
            case 2:
                elevationFragment = new ElevationGraphFragment();
                args = new Bundle();
                args.putString("track",trackname);
                elevationFragment.setArguments(args);
                return elevationFragment;
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return mNumOfTabs;
    }


}
