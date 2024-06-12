package com.bypriyan.m24.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bypriyan.m24.R;
import com.bypriyan.m24.channelsActivity.AllChannelActivity;
import com.bypriyan.m24.channelsActivity.CreateChannelActivity;
import com.bypriyan.m24.databinding.ActivityMainBinding;
import com.bypriyan.m24.databinding.ActivityUserProfileBinding;
import com.bypriyan.m24.model.ModelVideos;
import com.bypriyan.m24.utility.Constant;
import com.bypriyan.m24.utility.PreferenceManager;
import com.bypriyan.m24.viewModel.VideosViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserProfileActivity extends AppCompatActivity {

    private ActivityUserProfileBinding binding;
    private PreferenceManager preferenceManager;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }
        getWindow().setStatusBarColor(ContextCompat.getColor(UserProfileActivity.this,R.color.white));// set status background white
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(this);
        firebaseAuth = FirebaseAuth.getInstance();

        updateUserData();

        binding.cancelBtn.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.channelBtn.setOnClickListener(view -> {
            startActivity(new Intent(UserProfileActivity.this, AllChannelActivity.class));
        });

        binding.createChannelBtn.setOnClickListener(view -> {
            startActivity(new Intent(UserProfileActivity.this, CreateChannelActivity.class));
        });

        binding.notificationBtn.setOnClickListener(view -> {
            startActivity(new Intent(UserProfileActivity.this, NotificationActivity.class));
        });

        binding.termsBtn.setOnClickListener(view -> {
            startPageActivity("Terms and conditions", getString(R.string.terms));
        });

        binding.privecyPolicyBtn.setOnClickListener(view -> {
            startPageActivity("Privacy policy", getString(R.string.privecy));
        });

        binding.addCommunityPost.setOnClickListener(view -> {
            startActivity(new Intent(this, AddCommunityPostActivity.class));
        });

        binding.chatBtn.setOnClickListener(view -> {
            startActivity(new Intent(this, ChatUsersActivity.class));
        });

        binding.analyticsBtn.setOnClickListener(view -> {
            startActivity(new Intent(this, AnalyticsActivity.class));
        });

        binding.aboutUsBtn.setOnClickListener(view -> {
            startPageActivity("About us", getString(R.string.about));
        });

        binding.groupBtn.setOnClickListener(view -> {
            startActivity(new Intent(UserProfileActivity.this, CreateGroupActivity.class));
        });

        binding.feedBackBtn.setOnClickListener(view -> {
            startActivity(new Intent(UserProfileActivity.this, HelpAndFeedbackActivity.class));
        });

        binding.helpBtn.setOnClickListener(view -> {
            startActivity(new Intent(UserProfileActivity.this, SupportActivity.class));
        });
    }

    private void startPageActivity(String pageType, String content){
        Intent intent = new Intent(UserProfileActivity.this, PagesActivity.class);
        intent.putExtra("pageType", pageType);
        intent.putExtra("content", content);
        startActivity(intent);
    }

    private void updateUserData() {
        binding.userName.setText(preferenceManager.getString(Constant.KEY_NAME));
        binding.userEmail.setText(preferenceManager.getString(Constant.KEY_EMAIL));

        try {
            Glide.with(this).load(preferenceManager.getString(Constant.KEY_PROFILE_IMAGE))
                    .centerInside().placeholder(R.drawable.ic_person).into(binding.profileImage);
        }catch (Exception e){
        }
    }
}