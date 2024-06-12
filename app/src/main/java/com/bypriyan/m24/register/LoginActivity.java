package com.bypriyan.m24.register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bypriyan.m24.R;
import com.bypriyan.m24.activity.MainActivity;
import com.bypriyan.m24.databinding.ActivityLoginBinding;
import com.bypriyan.m24.model.ModelServerId;
import com.bypriyan.m24.utility.Constant;
import com.bypriyan.m24.utility.ListnerClass;
import com.bypriyan.m24.utility.PreferenceManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN=1000;
    private PreferenceManager preferenceManager;
    private ListnerClass list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }
        getWindow().setStatusBarColor(ContextCompat.getColor(LoginActivity.this, R.color.white));// set status background white

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        list = new ListnerClass();

        preferenceManager = new PreferenceManager(this);

        binding.googleBtn.setOnClickListener(view -> {
            loadingGoogle(true);
            signIn();

        });

        binding.facebookBtn.setOnClickListener(view -> {
            Toast.makeText(this, ""+"coming soon", Toast.LENGTH_SHORT).show();
        });

        binding.privacyPolicy.setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://m24app121.blogspot.com/2024/02/privacy-policy.html")));

        });

        binding.terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://m24app121.blogspot.com/2024/02/terms.html")));
            }
        });

        binding.skipBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
        });


    }

    //google sign in
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            firebaseAuth(account.getIdToken());
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("TAG", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuth(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    UploadUserData(user, user.getUid());
                } else {
                    // Handle the error
                }
            }
        });
    }

    private void UploadUserData(FirebaseUser account, String uid) {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constant.KEY_NAME, account.getDisplayName());
        hashMap.put(Constant.KEY_EMAIL, account.getEmail());
        hashMap.put(Constant.KEY_PROFILE_IMAGE, ""+account.getPhotoUrl().toString());
        hashMap.put(Constant.KEY_UID, mAuth.getUid());
        hashMap.put(Constant.KEY_ONLINE_STATUS, "offline");
        hashMap.put(Constant.KEY_TYPING_STATUS,"noOne");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_USERS);
        reference.child(uid).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                loadingGoogle(false);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingGoogle(false);
                Toast.makeText(LoginActivity.this, ""+e, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadingGoogle(boolean b){
        if(b){
            binding.googleBtn.setVisibility(View.GONE);
            binding.googleProgressbar.setVisibility(View.VISIBLE);
        }else {
            binding.googleBtn.setVisibility(View.VISIBLE);
            binding.googleProgressbar.setVisibility(View.GONE);
        }
    }

}