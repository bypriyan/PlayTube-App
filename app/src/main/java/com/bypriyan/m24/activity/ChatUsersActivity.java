package com.bypriyan.m24.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.bypriyan.m24.R;
import com.bypriyan.m24.adapter.AdapterUser;
import com.bypriyan.m24.databinding.ActivityChatUsersBinding;
import com.bypriyan.m24.databinding.ActivityNotificationBinding;
import com.bypriyan.m24.model.ModelUsers;
import com.bypriyan.m24.utility.Constant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ChatUsersActivity extends AppCompatActivity {

    private ActivityChatUsersBinding binding;
    private List<ModelUsers> usersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }
        getWindow().setStatusBarColor(ContextCompat.getColor(ChatUsersActivity.this,R.color.white));// set status background white
        binding = ActivityChatUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        usersList = new ArrayList<>();
        getAllUser();

        binding.backBtn.setOnClickListener(view -> {
            onBackPressed();
        });

    }

    private void getAllUser() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_USERS);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                usersList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    ModelUsers modelUsers = ds.getValue(ModelUsers.class);
                    if(!modelUsers.getUid().equals(user.getUid())){
                        usersList.add(modelUsers);
                    }
                    binding.UserRecyclearView.setAdapter(new AdapterUser(ChatUsersActivity.this, usersList));

                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }
}