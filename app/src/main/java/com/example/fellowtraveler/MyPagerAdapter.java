package com.example.fellowtraveler;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.fellowtraveler.Fragments.StatisticsFragment;
import com.example.fellowtraveler.Fragments.SpeedGraphFragment;
import com.example.fellowtraveler.Fragments.ThirdFragment;

public class MyPagerAdapter extends FragmentStateAdapter {
    int mNumOfTabs;
    String trackname;
    public MyPagerAdapter(FragmentActivity context, int NumOfTabs, String trackname) {
        super(context);
        this.mNumOfTabs = NumOfTabs;
        this.trackname = trackname;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                StatisticsFragment tab1 = new StatisticsFragment();
                return tab1;
            case 1:
                SpeedGraphFragment tab2 = new SpeedGraphFragment();
                Bundle args = new Bundle();
                args.putString("track",trackname);
                tab2.setArguments(args);
                return tab2;
            case 2:
                ThirdFragment tab3 = new ThirdFragment();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return mNumOfTabs;
    }


}
