package com.bypriyan.m24.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bypriyan.m24.R;
import com.bypriyan.m24.channelsActivity.AllChannelActivity;
import com.bypriyan.m24.databinding.ActivityAllChannelBinding;
import com.bypriyan.m24.databinding.ActivityPagesBinding;

public class PagesActivity extends AppCompatActivity {

    private ActivityPagesBinding binding;
    private String pageType, content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }
        getWindow().setStatusBarColor(ContextCompat.getColor(PagesActivity.this,R.color.white));// set status background white
        binding = ActivityPagesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        pageType = getIntent().getStringExtra("pageType");
        content = getIntent().getStringExtra("content");

        if(pageType==null || content==null){
            Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }

        binding.pageType.setText(pageType);
        binding.content.setText(content);

        binding.backBtn.setOnClickListener(view -> {
            onBackPressed();
        });
        
    }
}