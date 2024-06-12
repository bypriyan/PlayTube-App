package com.bypriyan.m24.channelsActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.bypriyan.m24.databinding.ActivityCreateChannelBinding;
import com.bypriyan.m24.utility.Constant;
import com.bypriyan.m24.utility.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;

public class CreateChannelActivity extends AppCompatActivity {

    private ActivityCreateChannelBinding binding;
    private final int REQ_PROFILE_IMAGE = 123;
    private final int REQ_COVER_IMAGE = 321;
    private Bitmap profileBitmap;
    private Bitmap coverImgBitmap;
    private StorageReference storageReference;
    private FirebaseUser user;
    private PreferenceManager preferenceManager;
    private String profileImageUrl, coverImageUrl;

    private String websiteUrl = "null", instagramUrl = "null", fbUrl = "null", twitterUrl = "null";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }
        getWindow().setStatusBarColor(ContextCompat.getColor(CreateChannelActivity.this, com.bypriyan.m24.R.color.white));// set status background white
        binding = ActivityCreateChannelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        user = FirebaseAuth.getInstance().getCurrentUser();
        preferenceManager = new PreferenceManager(this);
        storageReference = FirebaseStorage.getInstance().getReference();

        binding.profileRl.setOnClickListener(view -> {
            openGallery(REQ_PROFILE_IMAGE);
        });

        binding.coverRl.setOnClickListener(view -> {
            openGallery(REQ_COVER_IMAGE);
        });

        binding.backBtn.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.createBtn.setOnClickListener(view -> {
            isLoading(true);
            if(isValid()){
                checkLinks();
                uploadProfileImage();
            }else{
                isLoading(false);
            }
        });

    }

    private void uploadProfileImage() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        profileBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] finalImg = baos.toByteArray();
        final StorageReference filePath;
        filePath = storageReference.child("Channel_Profile_Img/").child(finalImg +"jpeg");
        final UploadTask uploadTask = filePath.putBytes(finalImg);
        uploadTask.addOnCompleteListener(CreateChannelActivity.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    profileImageUrl = String.valueOf(uri);
                                    uploadCoverImage();
                                }
                            });
                        }
                    });
                }else{
                    Toast.makeText(CreateChannelActivity.this, "Something went wrong..", Toast.LENGTH_SHORT).show();
                    isLoading(false);
                }
            }
        });
    }

    private void uploadCoverImage() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        coverImgBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] finalImg = baos.toByteArray();
        final StorageReference filePath;
        filePath = storageReference.child("Channel_Cover_Img/").child(finalImg +"jpeg");
        final UploadTask uploadTask = filePath.putBytes(finalImg);
        uploadTask.addOnCompleteListener(CreateChannelActivity.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    coverImageUrl = String.valueOf(uri);
                                    uploadInto();
                                }
                            });
                        }
                    });
                }else{
                    Toast.makeText(CreateChannelActivity.this, "Something went wrong..", Toast.LENGTH_SHORT).show();
                    isLoading(false);
                }
            }
        });
    }

    private void uploadInto() {
        
        String timeStamp = ""+System.currentTimeMillis();
        
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constant.KEY_UID, ""+user.getUid());
        hashMap.put(Constant.KEY_CHANNEL_PROFILE_IMAGE, ""+profileImageUrl);
        hashMap.put(Constant.KEY_CHANNEL_COVER_IMAGE, ""+coverImageUrl);
        hashMap.put(Constant.KEY_CHANNEL_NAME, ""+binding.channelName.getEditText().getText().toString());
        hashMap.put(Constant.KEY_CHANNEL_ABOUT, ""+binding.aboutChannel.getEditText().getText().toString());
        hashMap.put(Constant.KEY_CHANNEL_ID, ""+timeStamp);
        hashMap.put(Constant.KEY_CHANNEL_TIMESTAMP, ""+timeStamp);
        hashMap.put(Constant.KEY_CHANNEL_SUBSCRIBERS, "0");
        hashMap.put(Constant.KEY_CHANNEL_WEBSITE, websiteUrl);
        hashMap.put(Constant.KEY_CHANNEL_FB, ""+fbUrl);
        hashMap.put(Constant.KEY_CHANNEL_INSTAGRAM, ""+instagramUrl);
        hashMap.put(Constant.KEY_CHANNEL_TWITTER, ""+twitterUrl);
        
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_USERS);
        reference.child(user.getUid()).child(Constant.KEY_CHANNEL).child(timeStamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(CreateChannelActivity.this, "Channel created successfully", Toast.LENGTH_SHORT).show();
                        isLoading(false);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        Toast.makeText(CreateChannelActivity.this, ""+e, Toast.LENGTH_SHORT).show();
                        isLoading(false);
                    }
                });
        
    }

    private void checkLinks(){
        if(!binding.websiteUrl.getEditText().getText().toString().isEmpty()){
            websiteUrl = binding.websiteUrl.getEditText().getText().toString();
        }
        if(!binding.fbUrl.getEditText().getText().toString().isEmpty()){
            fbUrl = binding.fbUrl.getEditText().getText().toString();
        }
        if(!binding.instaUrl.getEditText().getText().toString().isEmpty()){
            instagramUrl = binding.instaUrl.getEditText().getText().toString();
        }
        if(!binding.twitterUrl.getEditText().getText().toString().isEmpty()){
            twitterUrl = binding.twitterUrl.getEditText().getText().toString();
        }
    }

    private void openGallery(int req) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, req);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if(requestCode == REQ_PROFILE_IMAGE){
                Uri uri = data.getData();
                try {
                    profileBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                binding.profileImageView.setImageBitmap(profileBitmap);
                binding.profileImgMaterialView.setVisibility(View.VISIBLE);
                binding.profileRl.setVisibility(View.GONE);
            }else if(requestCode == REQ_COVER_IMAGE){
                Uri uri = data.getData();
                try {
                    coverImgBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                binding.coverImageView.setImageBitmap(coverImgBitmap);
                binding.coverImgMaterialView.setVisibility(View.VISIBLE);
                binding.coverRl.setVisibility(View.GONE);
            }

        }
    }

    private boolean isValid(){
        if(profileBitmap==null){
            Toast.makeText(this, "Select Profile Image", Toast.LENGTH_SHORT).show();
            return false;
        }else if(coverImgBitmap==null){
            Toast.makeText(this, "Select Cover Image", Toast.LENGTH_SHORT).show();
            return false;
        }else if(binding.channelName.getEditText().getText().toString().isEmpty()){
            binding.channelName.setError("Empty");
            binding.channelName.requestFocus();
            return false;
        }else if(binding.aboutChannel.getEditText().getText().toString().isEmpty()){
            binding.aboutChannel.setError("Empty");
            binding.aboutChannel.requestFocus();
            return false;
        }else {
            return true;
        }
    }

    private void isLoading(boolean s){
        if(s){
            binding.createBtn.setVisibility(View.GONE);
            binding.progressbar.setVisibility(View.VISIBLE);
        }else{
            binding.createBtn.setVisibility(View.VISIBLE);
            binding.progressbar.setVisibility(View.GONE);
        }
    }

}