package com.bypriyan.m24.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bypriyan.m24.R;
import com.bypriyan.m24.databinding.ActivityHelpAndFeedbackBinding;
import com.bypriyan.m24.databinding.ActivitySupportBinding;

public class SupportActivity extends AppCompatActivity {

    private ActivitySupportBinding binding;
    static int PERMISSION_CODE= 100;
    private String supportNumber = "7974476357";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }
        getWindow().setStatusBarColor(ContextCompat.getColor(SupportActivity.this,R.color.white));// set status background white
        binding = ActivitySupportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.contect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!binding.name.getEditText().getText().toString().isEmpty() &&
                        !binding.subject.getEditText().getText().toString().isEmpty()  ){
                    sendMail(binding.name.getEditText().getText().toString());
                }else{
                    Toast.makeText(SupportActivity.this, "Email field can't be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(SupportActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){

                    ActivityCompat.requestPermissions(SupportActivity.this,new String[]{Manifest.permission.CALL_PHONE},PERMISSION_CODE);

                }else{
                    Intent i = new Intent(Intent.ACTION_CALL);
                    i.setData(Uri.parse("tel:"+supportNumber));
                    startActivity(i);
                }
            }
        });

        binding.whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isWhatappInstalled()){
                    String number = "+91"+supportNumber;
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone="+number+
                            "&text="+"Hello"));
                    startActivity(i);

                }else {

                    Toast.makeText(SupportActivity.this,"Whatsapp is not installed",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void sendMail(String s) {
        String []recipient = {"104priyanshu@gmail.com","104priyanshu@gmail.com"};
        String subject = "m24";

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, recipient);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, s);

        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, "Choose an email client"));
    }


    private boolean isWhatappInstalled(){

        PackageManager packageManager = getPackageManager();
        boolean whatsappInstalled;

        try {

            packageManager.getPackageInfo("com.whatsapp",PackageManager.GET_ACTIVITIES);
            whatsappInstalled = true;


        }catch (PackageManager.NameNotFoundException e){

            whatsappInstalled = false;

        }

        return whatsappInstalled;

    }
}