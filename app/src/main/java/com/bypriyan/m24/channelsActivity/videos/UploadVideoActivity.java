package com.bypriyan.m24.channelsActivity.videos;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bypriyan.m24.R;
import com.bypriyan.m24.channelsActivity.CreateChannelActivity;
import com.bypriyan.m24.databinding.ActivityUploadVideoBinding;
import com.bypriyan.m24.utility.Constant;
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
import com.iceteck.silicompressorr.SiliCompressor;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import io.reactivex.annotations.Nullable;

public class UploadVideoActivity extends AppCompatActivity {

    private ActivityUploadVideoBinding binding;
    private static final int PICK_VIDEO_REQUEST = 2;

    private final int REQ_THUMBNAIL_IMAGE = 123;
    private Bitmap selectedThumbnailImg;
    private String finalVideoPath;
    private ProgressBar progressBar;
    private FirebaseUser firebaseUser;
    private TextView uploadingText;
    private AlertDialog alertDialog;
    private TextView progressPercentage;
    private StorageReference storageReference;
    private String channelId;

    private String readVideoPermission = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) ? "android.permission.READ_MEDIA_VIDEO" : "android.permission.READ_EXTERNAL_STORAGE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }
        getWindow().setStatusBarColor(ContextCompat.getColor(UploadVideoActivity.this,R.color.white));// set status background white
        binding = ActivityUploadVideoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        channelId= getIntent().getStringExtra(Constant.KEY_VIDEO_CHANNEL_ID);

        if (ContextCompat.checkSelfPermission(this, readVideoPermission) == PackageManager.PERMISSION_GRANTED) {

        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissions(new String[]{Manifest.permission.READ_MEDIA_VIDEO}, 101);
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission
                        .WRITE_EXTERNAL_STORAGE}, 101);
            }
        }

        binding.thumbnailRl.setOnClickListener(view -> {
            openImgGallery();
        });

        binding.thumbnailImageView.setOnClickListener(view -> {
            openImgGallery();
        });

        binding.videoRl.setOnClickListener(view -> {
            openVideoGallery();
        });

        binding.videoView.setOnClickListener(view -> {
            openVideoGallery();
        });

        binding.backBtn.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.postBtn.setOnClickListener(view -> {
            if(isValid())uploadThumbnailImage();
        });

    }

    private void openImgGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQ_THUMBNAIL_IMAGE);
    }

    private void openVideoGallery() {
        if (ContextCompat.checkSelfPermission(this, readVideoPermission) == PackageManager.PERMISSION_GRANTED) {
            selectVideo();

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissions(new String[]{Manifest.permission.READ_MEDIA_VIDEO}, 101);
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission
                        .WRITE_EXTERNAL_STORAGE}, 101);
            }
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if(requestCode == REQ_THUMBNAIL_IMAGE){
                Uri uri = data.getData();
                try {
                    selectedThumbnailImg = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                binding.thumbnailImageView.setImageBitmap(selectedThumbnailImg);
                binding.thumbnailImgMaterialView.setVisibility(View.VISIBLE);
                binding.thumbnailRl.setVisibility(View.GONE);
            }else if(requestCode == PICK_VIDEO_REQUEST){

                    Uri uri = data.getData();
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
                    new UploadVideoActivity.CompressVideo().execute("false", uri.toString(), file.getPath());

            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    private void selectVideo() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(intent, "Select Video"),
                PICK_VIDEO_REQUEST);
    }

    private boolean isValid(){
        if(selectedThumbnailImg==null){
            Toast.makeText(this, "Select thumbnail Image", Toast.LENGTH_SHORT).show();
            return false;
        }else if(finalVideoPath==null){
            Toast.makeText(this, "Select Your video", Toast.LENGTH_SHORT).show();
            return false;
        }else if(binding.title.getEditText().getText().toString().isEmpty()){
            binding.title.setError("Empty");
            binding.title.requestFocus();
            return false;
        }else if(binding.description.getEditText().getText().toString().isEmpty()){
            binding.description.setError("Empty");
            binding.description.requestFocus();
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
        uploadingText = view.findViewById(R.id.uploadingText);
        progressPercentage = view.findViewById(R.id.progressPercentag);

        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

    }

    private void uploadThumbnailImage() {

        showUploadingDialog();
        uploadingText.setText("Uploading Thumbnail..");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        selectedThumbnailImg.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] finalImg = baos.toByteArray();
        final StorageReference filePath;
        filePath = storageReference.child("Thumbnail/").child(finalImg +"jpeg");
        final UploadTask uploadTask = filePath.putBytes(finalImg);

        uploadTask.addOnProgressListener(taskSnapshot -> {
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            int progPer = (int) progress;
            progressBar.setProgress(progPer);
            progressPercentage.setText(""+progPer+"%");
        });

        uploadTask.addOnCompleteListener(UploadVideoActivity.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@io.reactivex.annotations.NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String thumbnailImgUrl = String.valueOf(uri);
                                    uploadCompressedVideo(thumbnailImgUrl);
                                }
                            });
                        }
                    });
                }else{
                    Toast.makeText(UploadVideoActivity.this, "Something went wrong..", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadCompressedVideo(String thumbnailUrl) {

        uploadingText.setText("Uploading Video..");
        progressPercentage.setText("0");

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        String videoFileName = UUID.randomUUID().toString() + ".mp4";
        StorageReference videoRef = storageRef.child("videos/" + videoFileName);

        Uri videoUri = Uri.fromFile(new File(finalVideoPath));

        UploadTask uploadTask = videoRef.putFile(videoUri);

        uploadTask.addOnProgressListener(taskSnapshot -> {
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            int progPer = (int) progress;
            progressBar.setProgress(progPer);
            progressPercentage.setText(""+progPer+"%");

        });

        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            videoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String videoUrl = String.valueOf(uri);
                                    uploadVideoData(videoUrl, thumbnailUrl);
                                }
                            });
                        }
                    });
                }else{
                    Toast.makeText(UploadVideoActivity.this, "Failed..", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadVideoData(String videoUrl, String thumbnailUrl) {

        String timeStamp = String.valueOf(System.currentTimeMillis());

        HashMap<String, String> hashMap  = new HashMap<>();
        hashMap.put(Constant.KEY_VIDEO_URL, videoUrl);
        hashMap.put(Constant.KEY_VIDEO_TITLE, ""+binding.title.getEditText().getText().toString());
        hashMap.put(Constant.KEY_VIDEO_DESCRIPTION, ""+binding.description.getEditText().getText().toString());
        hashMap.put(Constant.KEY_VIDEO_LIKE, "0");
        hashMap.put(Constant.KEY_VIDEO_VIEWS, "0");
        hashMap.put(Constant.KEY_VIDEO_thumbnail, thumbnailUrl);
        hashMap.put(Constant.KEY_VIDEO_ID, ""+timeStamp);
        hashMap.put(Constant.KEY_VIDEO_UID, ""+firebaseUser.getUid());
        hashMap.put(Constant.KEY_VIDEO_TIMESTAMP, ""+timeStamp);
        hashMap.put(Constant.KEY_VIDEO_CHANNEL_ID, channelId);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_VIDEOS);
        reference.child(timeStamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(UploadVideoActivity.this, "video successfully uploaded", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UploadVideoActivity.this, ""+e, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private class CompressVideo extends AsyncTask<String, String, String> {

        Dialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(UploadVideoActivity.this, "", "Compressing...");
        }

        @Override
        protected String doInBackground(String... strings) {
            String videoPath = null;

            try {
                Uri uri = Uri.parse(strings[1]);videoPath = SiliCompressor.with(UploadVideoActivity.this)
                        .compressVideo(uri, strings[2]);

                finalVideoPath = videoPath;
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return videoPath;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();


            binding.videoView.setVisibility(View.VISIBLE);
            File file = new File(s);
            Uri uri = Uri.fromFile(file);
            binding.videoView.setVideoURI(uri);
            binding.videoView.start();
            binding.videoRl.setVisibility(View.GONE);
            binding.videoView.setVisibility(View.VISIBLE);

//            float size = file.length() / 1024f;
        }
    }

}