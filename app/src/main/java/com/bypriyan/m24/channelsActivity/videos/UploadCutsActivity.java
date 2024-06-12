package com.bypriyan.m24.channelsActivity.videos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bypriyan.m24.R;
import com.bypriyan.m24.channelsActivity.videos.UploadCutsActivity;
import com.bypriyan.m24.databinding.ActivityUploadCutsBinding;
import com.bypriyan.m24.utility.Constant;
import com.bypriyan.m24.viewModel.UploadCutsViewModel;
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
import com.iceteck.silicompressorr.SiliCompressor;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UploadCutsActivity extends AppCompatActivity {

    private ActivityUploadCutsBinding binding;
    private static final int PICK_VIDEO_REQUEST = 1;
    private static final long MAX_VIDEO_DURATION_MS = 60 * 1000; // 1 minute
    private FirebaseUser firebaseUser;
    private ProgressBar progressBar;
    private AlertDialog alertDialog;
    private TextView progressPercentage;
    private String channelId;
    private String readVideoPermission = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) ? "android.permission.READ_MEDIA_VIDEO" : "android.permission.READ_EXTERNAL_STORAGE";

    private String finalVideoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }
        getWindow().setStatusBarColor(ContextCompat.getColor(UploadCutsActivity.this,R.color.white));// set status background white
        binding = ActivityUploadCutsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        channelId = getIntent().getStringExtra(Constant.KEY_VIDEO_CHANNEL_ID);

        if (ContextCompat.checkSelfPermission(this, readVideoPermission) == PackageManager.PERMISSION_GRANTED) {

        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissions(new String[]{Manifest.permission.READ_MEDIA_VIDEO}, 101);
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission
                        .WRITE_EXTERNAL_STORAGE}, 101);
            }
        }


        binding.videoRl.setOnClickListener(view -> {
            openGallery();
        });

        binding.videoView.setOnClickListener(view -> {
            openGallery();
        });

        binding.backBtn.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.postBtn.setOnClickListener(view -> {

            if (isValid()) {
                uploadCompressedVideo();
            }
        });

    }

    private void uploadVideoData(String videoUrl) {
        String timeStamp = String.valueOf(System.currentTimeMillis());

        HashMap<String, String> hashMap  = new HashMap<>();
        hashMap.put(Constant.KEY_CUTS_VIDEO_URL, videoUrl);
        hashMap.put(Constant.KEY_CUTS_VIDEO_TITLE, ""+binding.title.getEditText().getText().toString());
        hashMap.put(Constant.KEY_CUTS_VIDEO_DESCRIPTION, ""+binding.description.getEditText().getText().toString());
        hashMap.put(Constant.KEY_CUTS_VIDEO_LIKE, "0");
        hashMap.put(Constant.KEY_CUTS_VIDEO_VIEWS, "0");
        hashMap.put(Constant.KEY_CUTS_VIDEO_ID, ""+timeStamp);
        hashMap.put(Constant.KEY_CUTS_VIDEO_UID, ""+firebaseUser.getUid());
        hashMap.put(Constant.KEY_CUTS_VIDEO_TIMESTAMP, ""+timeStamp);
        hashMap.put(Constant.KEY_VIDEO_CHANNEL_ID, channelId);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_CUTS);
        reference.child(timeStamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(UploadCutsActivity.this, "video successfully uploaded", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UploadCutsActivity.this, ""+e, Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });

    }

    private void openGallery() {
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

    private long getVideoDuration(Uri videoUri) {
        long duration = 0;

        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this, videoUri);

            String durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            duration = Long.parseLong(durationStr);

            retriever.release();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return duration;
    }
    private  boolean isValid(){
        if(finalVideoPath==null){
            Toast.makeText(this, "No video selected.", Toast.LENGTH_SHORT).show();
            return false;
        }else if(binding.title.getEditText().getText().toString().isEmpty()){
            binding.title.setError("Empty");
            binding.title.requestFocus();
            return false;
        }else if(binding.description.getEditText().getText().toString().isEmpty()){
            binding.description.setError("Empty");
            binding.description.requestFocus();
            return  false;
        }else{
            return true;
        }
    }

    private void showUploadingDialog() {

        View view = LayoutInflater.from(this).inflate(R.layout.uploading_video_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);

        progressBar = view.findViewById(R.id.progressBar);
        progressPercentage = view.findViewById(R.id.progressPercentag);

        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

    }


    private void uploadCompressedVideo() {

        showUploadingDialog();

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
                                    String url = String.valueOf(uri);
                                    uploadVideoData(url);
                                }
                            });
                        }
                    });
                }else{
                    Toast.makeText(UploadCutsActivity.this, "Failed..", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void selectVideo() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(intent, "Select Video"),
                100);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {

            Uri uri = data.getData();

            long videoDuration = getVideoDuration(uri);
            if (videoDuration <= MAX_VIDEO_DURATION_MS) {
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
                new CompressVideo().execute("false", uri.toString(), file.getPath());
            } else {
                Toast.makeText(this, "Video duration exceeds 1 minute.", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private class CompressVideo extends AsyncTask<String, String, String> {

        Dialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(UploadCutsActivity.this, "", "Compressing...");
        }

        @Override
        protected String doInBackground(String... strings) {
            String videoPath = null;

            try {
                Uri uri = Uri.parse(strings[1]);videoPath = SiliCompressor.with(UploadCutsActivity.this)
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
            binding.videoRl.setVisibility(View.GONE);
            binding.videoView.setVisibility(View.VISIBLE);

//            float size = file.length() / 1024f;
        }
    }


}
