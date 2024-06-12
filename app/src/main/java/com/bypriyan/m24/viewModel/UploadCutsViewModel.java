package com.bypriyan.m24.viewModel;

import static android.net.wifi.WifiConfiguration.Status.strings;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.SiliCompressor;

import java.io.File;
import java.net.URISyntaxException;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UploadCutsViewModel extends ViewModel {

    public enum CompressionResult {
        SUCCESS,
        FAILURE,
        URL


    }

    private final MutableLiveData<CompressionResult> compressionResultLiveData = new MutableLiveData<>();
    private MutableLiveData<String> uploadedVideoUriLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> uploadedProgressLiveData = new MutableLiveData<>();
    private final Executor executor = Executors.newSingleThreadExecutor();
    private String videoUrl = "null";
    String videoPath = null;


    public LiveData<CompressionResult> getCompressionResult() {
        return compressionResultLiveData;
    }

    public Context context;

    public void compressAndUploadVideo(String videoUri) {
        executor.execute(() -> {
            if (videoUri != null) {
                boolean uploadSuccess = compressVideo(videoUri);
                Log.d("TAG", "uploadSuccess: return");

                if (uploadSuccess) {
                    compressionResultLiveData.postValue(CompressionResult.SUCCESS);
                } else {
                    compressionResultLiveData.postValue(CompressionResult.FAILURE);
                    Log.d("TAG", "uploadSuccess: false");
                }
            } else {
                compressionResultLiveData.postValue(CompressionResult.FAILURE);
                Log.d("TAG", "compressAndUploadVideo: compressAndUploadVideo failed");
            }
        });
    }


    private boolean uploadCompressedVideo(String compressedVideoUri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        String videoFileName = UUID.randomUUID().toString() + ".mp4";
        StorageReference videoRef = storageRef.child("Cuts/" + videoFileName);

        // Upload file to Firebase Storage
        Uri uri = Uri.fromFile(new File(compressedVideoUri));
        UploadTask uploadTask = videoRef.putFile(uri);

        uploadTask.addOnProgressListener(taskSnapshot -> {
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            uploadedProgressLiveData.postValue((int) progress);
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
                                    uploadedVideoUriLiveData.postValue(url);
                                }
                            });
                        }
                    });
                }else{
                    compressionResultLiveData.postValue(CompressionResult.FAILURE);
                }
            }
        });

        return true; // Return true to indicate that the upload task is initiated
    }

    public boolean compressVideo(String videoUri){

        try {
            Uri uri = Uri.parse(strings[1]);
            videoPath = SiliCompressor.with(context).compressVideo(uri, strings[2]);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        if(videoPath!=null){
            return true;
        }else{
            return false;
        }
    }

    public LiveData<String> getUploadedVideoUri() {
        return uploadedVideoUriLiveData;
    }

    public LiveData<Integer> getUploadedProgressData() {
        return uploadedProgressLiveData;
    }




}