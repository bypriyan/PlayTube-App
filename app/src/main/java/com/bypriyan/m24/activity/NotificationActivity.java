package com.bypriyan.m24.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.bypriyan.m24.R;
import com.bypriyan.m24.channelsActivity.AllChannelActivity;
import com.bypriyan.m24.databinding.ActivityAllChannelBinding;
import com.bypriyan.m24.databinding.ActivityNotificationBinding;

public class NotificationActivity extends AppCompatActivity {

    private ActivityNotificationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }
        getWindow().setStatusBarColor(ContextCompat.getColor(NotificationActivity.this,R.color.white));// set status background white
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(view -> {
            onBackPressed();
        });
    }
}