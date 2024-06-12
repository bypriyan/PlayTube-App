package com.bypriyan.m24.register;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.bypriyan.m24.activity.MainActivity;
import com.bypriyan.m24.databinding.ActivityOtpBinding;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import io.reactivex.annotations.NonNull;

public class OtpActivity extends AppCompatActivity {

    private ActivityOtpBinding binding;
    private String phoneNum, verificationId, otpId, userName;
    private String profileImgUrl;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }
        getWindow().setStatusBarColor(ContextCompat.getColor(OtpActivity.this, com.bypriyan.m24.R.color.white));// set status background white

        binding = ActivityOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        phoneNum = getIntent().getStringExtra("phoneNumber");
        firebaseAuth = FirebaseAuth.getInstance();
        verificationId = getIntent().getStringExtra("verificationId").toString();

        if (!phoneNum.isEmpty()) {
            binding.phoneNumber.setText("Send on +91" + phoneNum);
            // Remove any non-numeric characters from the phone number
            phoneNum = phoneNum.replaceAll("[^0-9]", "");
            // Add the '+' sign and country code to the phone number
            phoneNum = "+91" + phoneNum; // Assuming India's country code is +91
        }

        initiateOtp();

        binding.otp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() == 6) {
                    // Enable the submit button
                    binding.confirmBtn.setEnabled(true);
                    binding.confirmBtn.setAlpha(1F);
                    //

                    loading(true);
                    if(verificationId !=null){
                        registerUser();
                    }else{
                        Toast.makeText(getApplicationContext(), "Invalid", Toast.LENGTH_SHORT).show();
                        loading(false);
                    }

                } else {
                    // Disable the submit button if the phone number length is not 10
                    binding.confirmBtn.setEnabled(false);
                    binding.confirmBtn.setAlpha(0.5F);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.confirmBtn.setOnClickListener(v -> {
            loading(true);
            if(verificationId !=null && (binding.otp.getText().toString().length() == 6)){
                registerUser();
            }else{
                Toast.makeText(getApplicationContext(), "Invalid", Toast.LENGTH_SHORT).show();
                loading(false);
            }
        });

        // Check and request permissions

    }
    private void registerUser() {
        String code = binding.otp.getText().toString();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //
                    Intent intent = new Intent(OtpActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("MobNum",phoneNum);
                    startActivity(intent);
                    finish();
                }else{
                    loading(false);
                    Toast.makeText(getApplicationContext(), "Invalid OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initiateOtp() {

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phoneNum)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                            @Override
                            public void onCodeSent(@NonNull @NotNull String s, @NonNull @NotNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                otpId = s;

                            }

                            @Override
                            public void onVerificationCompleted(@NonNull @NotNull PhoneAuthCredential phoneAuthCredential) {

                                signInWithPhoneAuthCredential(phoneAuthCredential);

                            }

                            @Override
                            public void onVerificationFailed(@NonNull @NotNull FirebaseException e) {
                                Toast.makeText(OtpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();


                            }
                        })          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                    Intent intent = new Intent(OtpActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("MobNum",phoneNum);
                    startActivity(intent);
                    finish();
                        } else {
                            Toast.makeText(OtpActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(OtpActivity.this, LoginActivity.class );
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

    private void loading(boolean isloading){
        if(isloading){
            binding.confirmBtn.setVisibility(View.INVISIBLE);
            binding.progressbar.setVisibility(View.VISIBLE);
        }else{
            binding.confirmBtn.setVisibility(View.VISIBLE);
            binding.progressbar.setVisibility(View.INVISIBLE);
        }

    }


}