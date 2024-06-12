package com.bypriyan.m24.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bypriyan.m24.R;
import com.bypriyan.m24.adapter.AdapterGroupChat;
import com.bypriyan.m24.adapter.AdapterGroupChatting;
import com.bypriyan.m24.databinding.ActivityGroupChatBinding;
import com.bypriyan.m24.model.ModelGroupChat;
import com.bypriyan.m24.utility.Constant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class GroupChatActivity extends AppCompatActivity {

    private ActivityGroupChatBinding binding;
    String groupId, groupIcon, groupName, myGroupRole="";
    private FirebaseAuth firebaseAuth;
    private final int REQ = 1;
    private Bitmap bitmap;
    String DownloadUrl="noImage";
    private ArrayList<ModelGroupChat> groupChateList;
    private AdapterGroupChatting adapterGroupChatting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.appColor));
        binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        groupId = getIntent().getStringExtra("groupId");
        groupIcon = getIntent().getStringExtra("groupIcon");
        groupName = getIntent().getStringExtra("groupName");

        firebaseAuth = FirebaseAuth.getInstance();

        loadGroupInfo();
        loadGroupMessages();

        loadMyGroupRole();

        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = binding.messageEt.getText().toString();
                if(TextUtils.isEmpty(message)){
                    Toast.makeText(GroupChatActivity.this, "Cannot send the empty message..", Toast.LENGTH_SHORT).show();
                }else {
                    sendMessage(message);
                }
                binding.messageEt.setText("");
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();

            }
        });

        binding.addParticipantBtn.setOnClickListener(view -> {
            Intent intent = new Intent(GroupChatActivity.this, AddPartivipantsActivity.class);
            intent.putExtra(Constant.KEY_GROUP_ID, groupId);
            startActivity(intent);
        });

        binding.infoBtn.setOnClickListener(view -> {
            Intent intent = new Intent(GroupChatActivity.this, GroupInfoActivity.class);
            intent.putExtra(Constant.KEY_GROUP_ID, groupId);
            startActivity(intent);
        });

    }



    private void loadMyGroupRole() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_GROUPS);
        reference.child(groupId).child(Constant.KEY_PARTICIPENTS)
                .orderByChild(Constant.KEY_UID).equalTo(firebaseAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                        for(DataSnapshot ds: snapshot.getChildren()){
                            myGroupRole = ""+ds.child(Constant.KEY_ROLE).getValue();
                            invalidateOptionsMenu();
                        }
                        if(myGroupRole.equals("creator") || myGroupRole.equals("admin")){
                            binding.addParticipantBtn.setVisibility(View.VISIBLE);
                        }else{
                            binding.addParticipantBtn.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

    }

    private void loadGroupMessages() {

        groupChateList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_GROUPS);
        reference.child(groupId).child(Constant.KEY_MESSAGE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                groupChateList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelGroupChat modelGroupChate = ds.getValue(ModelGroupChat.class);
                    groupChateList.add(modelGroupChate);
                }
                adapterGroupChatting = new AdapterGroupChatting(GroupChatActivity.this, groupChateList);
                binding.chatrecyclear.setAdapter(adapterGroupChatting);

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void sendMessage(String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_GROUPS);

        String timeStamp = String.valueOf(System.currentTimeMillis());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constant.KEY_MESSAGE, message);
        hashMap.put(Constant.KEY_SENDER, firebaseAuth.getCurrentUser().getUid());
        hashMap.put(Constant.KEY_TIMESTAMP, timeStamp);
        hashMap.put("type", "text");

        reference.child(groupId).child(Constant.KEY_MESSAGE).child(timeStamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {

                    }
                });
//        // list
//
//        DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference(Constant.KEY_CHATLIST)
//                .child(myUid).child(hisUid);
//
//        chatRef1.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                if(!snapshot.exists()){
//                    chatRef1.child("id").setValue(hisUid);
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull @NotNull DatabaseError error) {
//            }
//        });
//
//        DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference(Constant.KEY_CHATLIST)
//                .child(hisUid).child(myUid);
//
//        chatRef2.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                if(!snapshot.exists()){
//                    chatRef2.child("id").setValue(myUid);
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull @NotNull DatabaseError error) {
//            }
//        });
    }

    private void loadGroupInfo() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_GROUPS);
        reference.orderByChild(Constant.KEY_GROUP_ID).equalTo(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                for(DataSnapshot ds: snapshot.getChildren()){
                    String timeStamp = ""+ds.child(Constant.KEY_TIMESTAMP).getValue();
                    String createdBy = ""+ds.child(Constant.KEY_CREATED_BY).getValue();

                    binding.groupName.setText(groupName);
                    try{
                        Glide
                                .with(GroupChatActivity.this)
                                .load(groupIcon)
                                .centerCrop()
                                .placeholder(R.drawable.ic_person)
                                .into(binding.imageProfile);
                    }catch (Exception e){

                    }


                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void openGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQ);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ && resultCode == RESULT_OK){
            Uri uri = data.getData();
            sendImageMessage(uri);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
//            binding.noticeImageView.setImageBitmap(bitmap);
        }
    }

    private void sendImageMessage(Uri uri) {

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending image..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        String timestamp = String.valueOf(System.currentTimeMillis());

        String fileNameAndPath ="ChatImages/"+"post_"+timestamp;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte [] data = baos.toByteArray();

            StorageReference reference = FirebaseStorage.getInstance().getReference().child(fileNameAndPath);
            reference.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            DownloadUrl = uriTask.getResult().toString();
                            if(uriTask.isSuccessful()){
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_GROUPS);

                                String timeStamp = String.valueOf(System.currentTimeMillis());

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put(Constant.KEY_MESSAGE, DownloadUrl);
                                hashMap.put(Constant.KEY_SENDER, firebaseAuth.getCurrentUser().getUid());
                                hashMap.put(Constant.KEY_TIMESTAMP, timeStamp);
                                hashMap.put("type", "image");

                                reference.child(groupId).child(Constant.KEY_MESSAGE).child(timeStamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                binding.messageEt.setText("");
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull @NotNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(GroupChatActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                                            }
                                        });
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(GroupChatActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}