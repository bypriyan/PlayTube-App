package com.bypriyan.m24.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bypriyan.m24.R;
import com.bypriyan.m24.databinding.ActivityHelpAndFeedbackBinding;
import com.bypriyan.m24.utility.Constant;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class HelpAndFeedbackActivity extends AppCompatActivity {

    private ActivityHelpAndFeedbackBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }
        getWindow().setStatusBarColor(ContextCompat.getColor(HelpAndFeedbackActivity.this,R.color.white));// set status background white
        binding = ActivityHelpAndFeedbackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.sendBtn.setOnClickListener(view -> {
            if(binding.messageEt.getText().toString().isEmpty()){
                Toast.makeText(this, "empty", Toast.LENGTH_SHORT).show();
            }else{
                isLoading(true);
                sendData(binding.messageEt.getText().toString());
            }
        });

    }

    private void sendData(String string) {

        String timeStamp = ""+System.currentTimeMillis();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constant.KEY_TIMESTAMP, timeStamp);
        hashMap.put(Constant.KEY_MESSAGE, string);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("feedback");
        reference.child(timeStamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                isLoading(false);
                Toast.makeText(HelpAndFeedbackActivity.this, "feedback send successfully", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void isLoading(boolean s){
        if(s){
            binding.sendBtn.setVisibility(View.GONE);
            binding.progressbar.setVisibility(View.VISIBLE);
        }else{
            binding.sendBtn.setVisibility(View.VISIBLE);
            binding.progressbar.setVisibility(View.GONE);
        }
    }
}