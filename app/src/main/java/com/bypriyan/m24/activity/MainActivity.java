package com.bypriyan.m24.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bypriyan.m24.R;
import com.bypriyan.m24.databinding.ActivityMainBinding;
import com.bypriyan.m24.register.LoginActivity;
import com.bypriyan.m24.utility.Constant;
import com.bypriyan.m24.utility.PreferenceManager;
import com.bypriyan.m24.utility.Token;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    public ActivityMainBinding binding;
    private long backPressed;
    private Toast backToast;

    private BottomNavigationView bottomNavigationView;
    private NavController navController;
    private PreferenceManager preferenceManager;
    private GoogleSignInAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) throws NullPointerException{
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }
        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this,R.color.white));// set status background white

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        navController = Navigation.findNavController(this, R.id.frameLayout);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        preferenceManager = new PreferenceManager(this);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        account = GoogleSignIn.getLastSignedInAccount(this);
        
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                getToken();
                loadMessage();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUserDetails();
                    }
                });
            }
        });

        binding.profileImage.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
        });

        binding.notificationFrame.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, NotificationActivity.class));
        });

        binding.searchBtn.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, SearchVideoActivity.class));
        });

    }

    private void updateUserDetails() {
        if(account!= null){
            preferenceManager.putString(Constant.KEY_NAME, account.getDisplayName());
            preferenceManager.putString(Constant.KEY_EMAIL, account.getEmail());
            preferenceManager.putString(Constant.KEY_PROFILE_IMAGE, account.getPhotoUrl().toString());
            try {
                Glide.with(this).load(account.getPhotoUrl().toString())
                        .centerInside().placeholder(R.drawable.ic_person).into(binding.profileImage);
            }catch (Exception e){
            }
        }
    }

    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::UpdateToken );
    }
    private void UpdateToken(String token) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid= user.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_TOKENSUSER);
        Token token1 = new Token(token);
        reference.child(uid).setValue(token1);
    }

    private void loadMessage() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Messages");
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                try {
                    String fcmKey = ""+snapshot.child("key").getValue().toString();
                    preferenceManager.putString(Constant.KEY_FCM_SERVER_KEY, fcmKey);
                }catch (NullPointerException e){}

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }


    @Override
    public void onBackPressed() {
        if(backPressed+2500 > System.currentTimeMillis()){
            super.onBackPressed();
            backToast.cancel();
            return;
        }else{
            backToast =  Toast.makeText(this, "press again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }

        backPressed = System.currentTimeMillis();
    }
}