package com.bypriyan.m24.register;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.bypriyan.m24.R;
import com.bypriyan.m24.activity.MainActivity;
import com.bypriyan.m24.adapter.SliderAdapterViewPager;
import com.bypriyan.m24.databinding.ActivityIntroBinding;
import com.bypriyan.m24.other.SliderItems;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import io.reactivex.annotations.NonNull;

public class IntroActivity extends AppCompatActivity {
    private ActivityIntroBinding binding;
    private ArrayList<SliderItems> sliderItems;
    private Handler sliderHandler = new Handler();
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }
        getWindow().setStatusBarColor(ContextCompat.getColor(IntroActivity.this, com.bypriyan.m24.R.color.white));// set status background white


        binding = ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //init
        auth = FirebaseAuth.getInstance();
        sliderItems = new ArrayList<>();
        sliderItems.clear();

        //add data
        sliderItems.add(new SliderItems(R.raw.live_streaming,
                "Video Streaming ",
                "Enhance user engagement with seamless video streaming, delivering captivating content anytime, anywhere for an immersive viewing experience."));

        sliderItems.add(new SliderItems(R.raw.social_sharing,
                "Social sharing",
                "Connect, share, and engage with a vibrant community, amplifying content reach through seamless social sharing in our app."));

        sliderItems.add(new SliderItems(R.raw.playlist,
                "Playlists ",
                "Curate and organize personalized collections of videos, enhancing discovery and enjoyment, with our app's dynamic playlist feature."));


        sliderItems.add(new SliderItems(R.raw.tranding_video,
                "Trending and Viral Videos ",
                "Explore captivating, trending videos captivating the internet, staying updated with viral sensations in our app's spotlight."));


        sliderItems.add(new SliderItems(R.raw.comment,
                "Comments and Interaction ",
                "Engage in lively discussions, share thoughts, and foster community interactions through comments, enhancing the content experience in our app."));


        loadImages();

        binding.startButton.setOnClickListener(v -> {
            startActivity(new Intent(IntroActivity.this, LoginActivity.class));
            finish();
        });

        binding.skipBtn.setOnClickListener(v -> {
            startActivity(new Intent(IntroActivity.this, LoginActivity.class));
            finish();
        });
    }

    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            binding.viewPagerImageSlider.setCurrentItem(binding.viewPagerImageSlider.getCurrentItem() + 1);
        }
    };


    private void loadImages() {
        binding.viewPagerImageSlider.setAdapter(new SliderAdapterViewPager(sliderItems, binding.viewPagerImageSlider, IntroActivity.this));
        binding.viewPagerImageSlider.setClipToPadding(false);
        binding.viewPagerImageSlider.setClipChildren(false);
        binding.viewPagerImageSlider.setOffscreenPageLimit(3);
        binding.viewPagerImageSlider.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(50));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull @NotNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);

            }
        });

        binding.viewPagerImageSlider.setPageTransformer(compositePageTransformer);
        binding.viewPagerImageSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 6000);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 6000);
    }

    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }

    private void checkUserStatus(){
        FirebaseUser user = auth.getCurrentUser();
        if(user != null){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

}