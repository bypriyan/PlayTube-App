package com.bypriyan.m24.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bypriyan.m24.R;
import com.bypriyan.m24.adapter.AdapterParticipentAdd;
import com.bypriyan.m24.databinding.ActivityGroupChatBinding;
import com.bypriyan.m24.databinding.ActivityGroupInfoBinding;
import com.bypriyan.m24.model.ModelUsers;
import com.bypriyan.m24.utility.Constant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class GroupInfoActivity extends AppCompatActivity {

    private ActivityGroupInfoBinding binding;
    String groupId, myGroupRole ="", groupIcons, groupTitles, groupDescriptions;
    FirebaseAuth firebaseAuth;
    ArrayList<ModelUsers> usersList;
    AdapterParticipentAdd adapterParticipentAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.appColor));
        binding = ActivityGroupInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        groupId = getIntent().getStringExtra(Constant.KEY_GROUP_ID);

        loadGroupInfo();
        loadMyGroupRole();

        binding.addParticipantsGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupInfoActivity.this, AddPartivipantsActivity.class);
                intent.putExtra(Constant.KEY_GROUP_ID, groupId);
                startActivity(intent);
            }
        });

        binding.leaveGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dialogTitle = "";
                String dialogDescription = "";
                String positiveButtonTitle ="";

                if(myGroupRole.equals("creator")){
                    dialogTitle = "Delete Group";
                    dialogDescription = "Are you sure you want to Delete group? ";
                    positiveButtonTitle ="Delete";

                }else{
                    dialogTitle = "Leave Group";
                    dialogDescription = "Are you sure you want to Leave group? ";
                    positiveButtonTitle ="Leave";
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(GroupInfoActivity.this);
                builder.setTitle(dialogTitle).setMessage(dialogDescription)
                        .setPositiveButton(positiveButtonTitle, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if(myGroupRole.equals("creator")){
                                    deleteGroup();

                                }else{
                                    leaveGroup();

                                }

                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();

                            }
                        }).create().show();
            }
        });

        binding.editGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupInfoActivity.this, EditGroupActivity.class);
                intent.putExtra(Constant.KEY_GROUP_ID, groupId);
                intent.putExtra(Constant.KEY_GROUP_ICON, groupIcons);
                intent.putExtra(Constant.KEY_GROUP_TITLE, groupTitles);
                intent.putExtra(Constant.KEY_GROUP_DESCRIPTION, groupDescriptions);

                startActivity(intent);
            }
        });

    }

    private void leaveGroup() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_GROUPS);
        reference.child(groupId).child(Constant.KEY_PARTICIPENTS).child(firebaseAuth.getUid()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(GroupInfoActivity.this, "Group left Successfully...", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(GroupInfoActivity.this, MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(GroupInfoActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void deleteGroup() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_GROUPS);
        reference.child(groupId).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(GroupInfoActivity.this, "Group Deleted Successfully...", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(GroupInfoActivity.this, MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(GroupInfoActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void loadMyGroupRole() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_GROUPS);
        reference.child(groupId).child(Constant.KEY_PARTICIPENTS).orderByChild(Constant.KEY_UID)
                .equalTo(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                        for(DataSnapshot ds : snapshot.getChildren()){
                            myGroupRole  = ""+ds.child(Constant.KEY_ROLE).getValue();

                            if(myGroupRole.equals(Constant.KEY_PARTICIPENTS)){
                                binding.editGroup.setVisibility(View.GONE);
                                binding.addParticipantsGroup.setVisibility(View.GONE);
                                binding.leaveGroup.setText("Leave Group");
                            }
                            else if(myGroupRole.equals("admin")){
                                binding.editGroup.setVisibility(View.GONE);
                                binding.addParticipantsGroup.setVisibility(View.VISIBLE);
                                binding.leaveGroup.setText("Leave Group");
                            }
                            else if(myGroupRole.equals("creator")){
                                binding.editGroup.setVisibility(View.VISIBLE);
                                binding.addParticipantsGroup.setVisibility(View.VISIBLE);
                                binding.leaveGroup.setText("Delete Group");
                            }
                        }
                        loadParticipants();

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

    }

    private void loadParticipants() {

        usersList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_GROUPS);
        reference.child(groupId).child(Constant.KEY_PARTICIPENTS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                usersList.clear();
                for(DataSnapshot ds : snapshot.getChildren() ){
                    String uid = ""+ds.child(Constant.KEY_UID).getValue();

                    DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference(Constant.KEY_USERS);
                    ref1.orderByChild(Constant.KEY_UID).equalTo(uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                            for(DataSnapshot ds: snapshot.getChildren()){
                                ModelUsers modelUsers = ds.getValue(ModelUsers.class);
                                usersList.add(modelUsers);
                            }
                            adapterParticipentAdd = new AdapterParticipentAdd(GroupInfoActivity.this, usersList, ""+groupId,""+myGroupRole);
                            binding.participantsRv.setAdapter(adapterParticipentAdd);
                            binding.participantsTv.setText("Participants("+usersList.size()+")");

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

    private void loadGroupInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_GROUPS);
        reference.orderByChild(Constant.KEY_GROUP_ID).equalTo(groupId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                        for(DataSnapshot ds: snapshot.getChildren()){
                            String groupIds = ""+ds.child(Constant.KEY_GROUP_ID).getValue();
                            String groupTitle = ""+ds.child(Constant.KEY_GROUP_TITLE).getValue();
                            String groupDescription = ""+ds.child(Constant.KEY_GROUP_DESCRIPTION).getValue();
                            String groupIcon= ""+ds.child(Constant.KEY_GROUP_ICON).getValue();
                            String craetedBy= ""+ds.child(Constant.KEY_CREATED_BY).getValue();
                            String timeStamp= ""+ds.child(Constant.KEY_GROUP_TIMESTAMP).getValue();
                            groupIcons = ""+ds.child(Constant.KEY_GROUP_ICON).getValue();
                            groupDescriptions = ""+ds.child(Constant.KEY_GROUP_DESCRIPTION).getValue();
                            groupTitles = ""+ds.child(Constant.KEY_GROUP_TITLE).getValue();

                            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                            cal.setTimeInMillis(Long.parseLong(timeStamp));
                            String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();

                            loadCreaterInfo(dateTime, craetedBy);
                            binding.groupDescriptionTv.setText(groupDescription);

                            try{
                                Glide
                                        .with(GroupInfoActivity.this)
                                        .load(groupIcon)
                                        .centerCrop()
                                        .placeholder(R.drawable.ic_image)
                                        .into(binding.groupIcon);
                            }catch (Exception e){

                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

    private void loadCreaterInfo(String dateTime, String craetedBy) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_USERS);
        reference.orderByChild(Constant.KEY_UID).equalTo(craetedBy).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                for(DataSnapshot ds : snapshot.getChildren()){
                    String name = ""+ds.child(Constant.KEY_NAME).getValue();

                    binding.createdByTv.setText("Created by "+ name +" on "+dateTime);
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }
}