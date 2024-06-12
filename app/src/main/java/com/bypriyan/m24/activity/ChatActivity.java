package com.bypriyan.m24.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bypriyan.m24.R;
import com.bypriyan.m24.adapter.AdapterChat;
import com.bypriyan.m24.databinding.ActivityAddCommunityPostBinding;
import com.bypriyan.m24.databinding.ActivityChatBinding;
import com.bypriyan.m24.model.ModelChat;
import com.bypriyan.m24.model.ModelUsers;
import com.bypriyan.m24.utility.Constant;
import com.bypriyan.m24.utility.Token;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    private String myUid, hisUid,hisImage, hisName;
    private ActivityChatBinding binding;
    ValueEventListener seenListner;

    List<ModelChat> chatList;
    AdapterChat adapterChat;

    private final int REQ = 1;
    private Bitmap bitmap;
    String DownloadUrl="noImage";

    boolean isBlocked = false;
    boolean notify = false;
    DatabaseReference userRefForSeen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        hisUid = getIntent().getStringExtra(Constant.KEY_UID);
        myUid = user.getUid();
        hisImage = getIntent().getStringExtra(Constant.KEY_IMAGE);
        hisName = getIntent().getStringExtra(Constant.KEY_NAME);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);

        binding.chatrecyclear.setHasFixedSize(true);
        binding.chatrecyclear.setLayoutManager(layoutManager);









































































        try {

            Glide
                    .with(ChatActivity.this)
                    .load(hisImage)
                    .centerCrop()
                    .placeholder(R.drawable.ic_person)
                    .into(binding.imageProfile);
        }catch (Exception e){

        }

        //users info
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_USERS);

        Query userQuery = reference.orderByChild(Constant.KEY_UID).equalTo(hisUid);

        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds : snapshot.getChildren()){
                    String onlineStatus = ""+ds.child(Constant.KEY_ONLINE_STATUS).getValue();
                    String typingStatus = ""+ds.child(Constant.KEY_TYPING_STATUS).getValue();
                    String img = ""+ds.child(Constant.KEY_IMAGE).getValue();
                    String nam = ""+ds.child(Constant.KEY_NAME).getValue();
                    hisImage = img;

                    binding.name.setText(nam);
                    try {
                        Glide
                                .with(ChatActivity.this)
                                .load(img)
                                .centerCrop()
                                .placeholder(R.drawable.ic_person)
                                .into(binding.imageProfile);
                    }catch (Exception e){

                    }

                    if(typingStatus.equals(myUid)){
                        binding.userStatus.setText("Typing...");

                    }else{

                        if(onlineStatus.equals("online")){
                            binding.userStatus.setText(onlineStatus);}
//                        }else{
//                            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
//                            cal.setTimeInMillis(Long.parseLong(onlineStatus));
//                            String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();
//
//                            binding.userStatus.setText("Last seen at : "+dateTime);
//                        }
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.name.setText(hisName);
        try {
            Glide
                    .with(ChatActivity.this)
                    .load(hisImage)
                    .centerCrop()
                    .placeholder(R.drawable.ic_person)
                    .into(binding.imageProfile);
        }catch (Exception e){

        }
        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;
                String message = binding.messageEt.getText().toString();
                if(TextUtils.isEmpty(message)){
                    Toast.makeText(ChatActivity.this, "Cannot send the empty message..", Toast.LENGTH_SHORT).show();
                }else {
                    sendMessage(message);
                }
                binding.messageEt.setText("");

            }
        });

        binding.attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();

            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.messageEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {
                if(s.toString().trim().length()==0){
                    checkTypingStatus("noOne");
                }else{
                    checkTypingStatus(hisUid);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.blockIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isBlocked){
                    unblockUser();
                }else{
                    blockUser();

                }
            }
        });
        redMessage();
        checkHisBlocked();
        seenMessage();

    }

    private void checkHisBlocked() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_USERS);
        reference.child(myUid).child(Constant.KEY_BLOCKEDUSERS).orderByChild(Constant.KEY_UID).equalTo(hisUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                        for(DataSnapshot ds: snapshot.getChildren()){
                            if(ds.exists()){
                                binding.blockIv.setImageResource(R.drawable.block);
                                isBlocked = true;
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

    private void blockUser() {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constant.KEY_UID, hisUid);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_USERS);
        reference.child(myUid).child(Constant.KEY_BLOCKEDUSERS).child(hisUid).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        binding.blockIv.setImageResource(R.drawable.block);
                        Toast.makeText(ChatActivity.this, "Blocked successfully...", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(ChatActivity.this, " "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void unblockUser() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_USERS);
        reference.child(myUid).child(Constant.KEY_BLOCKEDUSERS).orderByChild(Constant.KEY_UID).equalTo(hisUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                        for(DataSnapshot ds : snapshot.getChildren()){

                            if(ds.exists()){
                                ds.getRef().removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                binding.blockIv.setImageResource(R.drawable.unblocked);
                                                Toast.makeText(ChatActivity.this, "Unblocked user...", Toast.LENGTH_SHORT).show();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull @NotNull Exception e) {
                                                Toast.makeText(ChatActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                                            }
                                        });
                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

    private void seenMessage() {
        userRefForSeen = FirebaseDatabase.getInstance().getReference(Constant.KEY_CHATS);
        seenListner = userRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if(chat.getReciver().equals(myUid) && chat.getSender().equals(hisUid) ){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put(Constant.KEY_SEEN, true);

                        ds.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void redMessage() {

        chatList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_CHATS);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                chatList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if(chat.getReciver().equals(myUid) && chat.getSender().equals(hisUid) ||
                            chat.getReciver().equals(hisUid) && chat.getSender().equals(myUid)){

                        chatList.add(chat);
                    }
                    adapterChat = new AdapterChat(ChatActivity.this, chatList,getIntent().getStringExtra(Constant.KEY_IMAGE));
                    adapterChat.notifyDataSetChanged();

                    binding.chatrecyclear.setAdapter(adapterChat);
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void sendMessage(String message) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        String timeStamp = String.valueOf(System.currentTimeMillis());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constant.KEY_MESSAGE, message);
        hashMap.put(Constant.KEY_RECIVER, hisUid);
        hashMap.put(Constant.KEY_SENDER, myUid);
        hashMap.put(Constant.KEY_TIMESTAMP, timeStamp);
        hashMap.put("type", "text");
        hashMap.put(Constant.KEY_SEEN, false);

        databaseReference.child(Constant.KEY_CHATS).push().setValue(hashMap);



        DatabaseReference database = FirebaseDatabase.getInstance().getReference(Constant.KEY_USERS).child(myUid);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                ModelUsers users = snapshot.getValue(ModelUsers.class);
                if (notify) {
                    SendNotification(hisUid, users.getName(), message);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


        // list

        DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference(Constant.KEY_CHATLIST)
                .child(myUid).child(hisUid);

        chatRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    chatRef1.child("id").setValue(hisUid);
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });

        DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference(Constant.KEY_CHATLIST)
                .child(hisUid).child(myUid);

        chatRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    chatRef2.child("id").setValue(myUid);
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });
    }

    private void SendNotification(String hisUid, String name, String message) {

    }

    private void checkOnlineStatus(String status){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Constant.KEY_USERS).child(myUid);
        HashMap<String , Object> hashMap = new HashMap<>();
        hashMap.put(Constant.KEY_ONLINE_STATUS,status);
        databaseReference.updateChildren(hashMap);

    }

    private void checkTypingStatus(String typing){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Constant.KEY_USERS).child(myUid);
        HashMap<String , Object> hashMap = new HashMap<>();
        hashMap.put(Constant.KEY_TYPING_STATUS,typing);
        databaseReference.updateChildren(hashMap);

    }
    @Override
    protected void onStart() {
        checkOnlineStatus("online");
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        String timeStamp = String.valueOf(System.currentTimeMillis());

        checkOnlineStatus(timeStamp);

        checkTypingStatus("noOne");

        userRefForSeen.removeEventListener(seenListner);
    }

    @Override
    protected void onResume() {
        checkOnlineStatus("online");
        super.onResume();
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

    private void sendImageMessage(Uri image_uri) {
        notify = true;
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending image..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        String timestamp = String.valueOf(System.currentTimeMillis());

        String fileNameAndPath ="ChatImages/"+"post_"+timestamp;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),image_uri);
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
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put(Constant.KEY_SENDER, myUid);
                                hashMap.put(Constant.KEY_RECIVER, hisUid);
                                hashMap.put(Constant.KEY_MESSAGE, DownloadUrl);
                                hashMap.put(Constant.KEY_TIMESTAMP, timestamp);
                                hashMap.put("type", "image");
                                hashMap.put(Constant.KEY_SEEN, false);

                                databaseReference.child(Constant.KEY_CHATS).push().setValue(hashMap);

                                DatabaseReference database = FirebaseDatabase.getInstance().getReference(Constant.KEY_USERS).child(myUid);
                                database.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                        ModelUsers users = snapshot.getValue(ModelUsers.class);
                                        if(notify) {
                                            SendNotification(hisUid, users.getName(),"Send you a photo...");

                                        }
                                        notify = false;


                                    }

                                    @Override
                                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                    }
                                });
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ChatActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

            DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference(Constant.KEY_CHATLIST)
                    .child(myUid).child(hisUid);

            chatRef1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    if(!snapshot.exists()){
                        chatRef1.child("id").setValue(hisUid);
                    }
                }
                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                }
            });

            DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference(Constant.KEY_CHATLIST)
                    .child(hisUid).child(myUid);

            chatRef2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    if(!snapshot.exists()){
                        chatRef2.child("id").setValue(myUid);
                    }
                }
                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}