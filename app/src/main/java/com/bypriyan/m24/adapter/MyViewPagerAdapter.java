package com.bypriyan.m24.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.bypriyan.m24.ui.chatting.all.ChattingFragment;
import com.bypriyan.m24.ui.chatting.all.CommunityFragment;
import com.bypriyan.m24.ui.chatting.all.GroupChattingFragment;

public class MyViewPagerAdapter  extends FragmentStateAdapter {
    public MyViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new CommunityFragment();
            case 1:
                return new ChattingFragment();
            case 2:
                return new GroupChattingFragment();
            default:
                return new CommunityFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
