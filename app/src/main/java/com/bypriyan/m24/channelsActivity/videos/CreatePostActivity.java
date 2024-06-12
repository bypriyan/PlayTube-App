package com.bypriyan.m24.channelsActivity.videos;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bypriyan.m24.R;
import com.bypriyan.m24.channelsActivity.CreateChannelActivity;
import com.bypriyan.m24.databinding.ActivityCreatePostBinding;
import com.bypriyan.m24.databinding.ActivityUploadCutsBinding;
import com.bypriyan.m24.utility.Constant;
import com.bypriyan.m24.utility.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
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

public class CreatePostActivity extends AppCompatActivity {

    private ActivityCreatePostBinding binding;
    private final int REQ_POSTIMG_IMAGE = 123;
    private StorageReference storageReference;
    private Bitmap postBitmap;
    private FirebaseUser user;
    private ProgressBar progressBar;
    private AlertDialog alertDialog;
    private TextView progressPercentage, uploadingText, textTwo;
    private String channelId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }
        getWindow().setStatusBarColor(ContextCompat.getColor(CreatePostActivity.this,R.color.white));// set status background white
        binding = ActivityCreatePostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //init
        user = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        channelId= getIntent().getStringExtra(Constant.KEY_VIDEO_CHANNEL_ID);

        binding.backBtn.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.postRl.setOnClickListener(view -> {
            openGallery();
        });

        binding.postImageView.setOnClickListener(view -> {
            openGallery();
        });

        binding.postBtn.setOnClickListener(view -> {
            if(isValid()){
                showUploadingDialog();
                uploadPostImage();
            }
        });

    }

    private void uploadPostImage() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        postBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] finalImg = baos.toByteArray();
        final StorageReference filePath;
        filePath = storageReference.child("POST/").child(finalImg +"jpeg");
        final UploadTask uploadTask = filePath.putBytes(finalImg);

        uploadTask.addOnProgressListener(taskSnapshot -> {
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            progressBar.setProgress((int) progress);
            progressPercentage.setText(""+(int) progress+"%");
        });

        uploadTask.addOnCompleteListener(CreatePostActivity.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String postImgUrl = String.valueOf(uri);
                                    uploadPostData(postImgUrl);
                                }
                            });
                        }
                    });
                }else{
                    Toast.makeText(CreatePostActivity.this, "Something went wrong..", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadPostData(String postImgUrl) {
        String timeStamp = ""+System.currentTimeMillis();

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constant.KEY_POST_UID, ""+user.getUid());
        hashMap.put(Constant.KEY_POST_IMG_URL, ""+postImgUrl);
        hashMap.put(Constant.KEY_POST_TITLE, ""+binding.title.getEditText().getText().toString());
        hashMap.put(Constant.KEY_POST_LIKE, "0");
        hashMap.put(Constant.KEY_POST_ID, ""+timeStamp);
        hashMap.put(Constant.KEY_POST_TIMESTAMP, ""+timeStamp);
        hashMap.put(Constant.KEY_VIDEO_CHANNEL_ID, channelId);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_POST);
        reference.child(timeStamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(CreatePostActivity.this, "Post Uploaded successfully", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        Toast.makeText(CreatePostActivity.this, ""+e, Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }
                });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQ_POSTIMG_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQ_POSTIMG_IMAGE) {

                Uri uri = data.getData();
                try {
                    postBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                binding.postImageView.setImageBitmap(postBitmap);
                binding.postImgMaterialView.setVisibility(View.VISIBLE);
                binding.postRl.setVisibility(View.GONE);

        }
    }

    private boolean isValid(){
        if(postBitmap==null){
            Toast.makeText(this, "Select post Image", Toast.LENGTH_SHORT).show();
            return false;
        }else if(binding.title.getEditText().getText().toString().isEmpty()){
            binding.title.setError("Empty");
            binding.title.requestFocus();
            return false;
        }else {
            return true;
        }
    }


    private void showUploadingDialog() {

        View view = LayoutInflater.from(this).inflate(R.layout.uploading_video_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);

        progressBar = view.findViewById(R.id.progressBar);
        progressPercentage = view.findViewById(R.id.progressPercentag);
        textTwo = view.findViewById(R.id.textTwo);
        uploadingText = view.findViewById(R.id.uploadingText);


        uploadingText.setText("Uploading Post..");
        textTwo.setText("Do not press back button your post is uploading.");

        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

}