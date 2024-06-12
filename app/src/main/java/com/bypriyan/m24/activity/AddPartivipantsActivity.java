package com.bypriyan.m24.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;

import com.bypriyan.m24.R;
import com.bypriyan.m24.adapter.AdapterParticipentAdd;
import com.bypriyan.m24.databinding.ActivityAddPartivipantsBinding;
import com.bypriyan.m24.databinding.ActivityGroupChatBinding;
import com.bypriyan.m24.model.ModelUsers;
import com.bypriyan.m24.utility.Constant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AddPartivipantsActivity extends AppCompatActivity {

    private ActivityAddPartivipantsBinding binding;
    FirebaseAuth firebaseAuth;
    String groupId, myGroupRole;
    private ArrayList<ModelUsers> usersList;
    AdapterParticipentAdd adapterParticipentAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.appColor));
        binding = ActivityAddPartivipantsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        groupId = getIntent().getStringExtra(Constant.KEY_GROUP_ID);

        loadGroupInfo();

        binding.backBtn.setOnClickListener(view -> {
            onBackPressed();
        });

    }

    private void getAllUsers() {
        usersList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_USERS);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                usersList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelUsers modelUsers = ds.getValue(ModelUsers.class);
                    if(!firebaseAuth.getUid().equals(modelUsers.getUid())){
                        usersList.add(modelUsers);
                    }
                }
                adapterParticipentAdd = new AdapterParticipentAdd(AddPartivipantsActivity.this, usersList, ""+groupId,""+myGroupRole);
                binding.usersRv.setAdapter(adapterParticipentAdd);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


    }

    private void loadGroupInfo() {

        Log.d("okay", "onDataChange: successfully ");

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference(Constant.KEY_GROUPS);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_GROUPS);
        reference.orderByChild(Constant.KEY_GROUP_ID).equalTo(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    String groupIds = ""+ds.child(Constant.KEY_GROUP_ID).getValue();
                    String groupTitle = ""+ds.child(Constant.KEY_GROUP_TITLE).getValue();
                    String groupDescription = ""+ds.child(Constant.KEY_GROUP_DESCRIPTION).getValue();
                    String groupIcon = ""+ds.child(Constant.KEY_GROUP_ICON).getValue();
                    String createdBy = ""+ds.child(Constant.KEY_CREATED_BY).getValue();
                    String timeStamp = ""+ds.child(Constant.KEY_TIMESTAMP).getValue();

                    reference1.child(groupIds).child(Constant.KEY_PARTICIPENTS).child(firebaseAuth.getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        myGroupRole = ""+snapshot.child(Constant.KEY_ROLE).getValue();
                                        getAllUsers();
                                        Log.d("okay", "onDataChange: successfully 3  ");

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                }
                            });

                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }
}