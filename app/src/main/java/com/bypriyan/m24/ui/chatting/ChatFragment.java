package com.bypriyan.m24.ui.chatting;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bypriyan.m24.R;
import com.bypriyan.m24.adapter.MyViewPagerAdapter;
import com.bypriyan.m24.databinding.FragmentChatBinding;
import com.bypriyan.m24.databinding.FragmentCutsBinding;
import com.google.android.material.tabs.TabLayout;

public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;

    MyViewPagerAdapter myViewPagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(getLayoutInflater());

        myViewPagerAdapter = new MyViewPagerAdapter(getActivity());
        binding.viewPager.setAdapter(myViewPagerAdapter);

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.tabLayout.getTabAt(position).select();
            }
        });

        return binding.getRoot();
    }

}