package com.bypriyan.m24.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.bypriyan.m24.R;
import com.bypriyan.m24.databinding.ActivityAddCommunityPostBinding;
import com.bypriyan.m24.utility.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import io.reactivex.annotations.Nullable;

public class AddCommunityPostActivity extends AppCompatActivity {

    private ActivityAddCommunityPostBinding binding;
    private final int REQ_POSTIMG_IMAGE = 123;
    private Bitmap bitmap;
    private DatabaseReference reference;
    private StorageReference storageReference;
    String DownloadUrl="noImage";
    String name, email, uid, dp;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddCommunityPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //
        reference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);

        uid = FirebaseAuth.getInstance().getUid().toString();
        loadUserData();

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
            if(binding.title.getEditText().getText().toString().isEmpty()){
                binding.title.setError("Empty");
                binding.title.requestFocus();
            }else if(binding.description.getEditText().getText().toString().isEmpty()){
                binding.description.setError("Empty");
                binding.description.requestFocus();
            }else if(bitmap==null){
                uploadData();

            }else{
                uploadImg();
            }
        });

    }

    private void loadUserData() {
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference(Constant.KEY_USERS);
        reference1.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                email = ""+snapshot.child(Constant.KEY_EMAIL).getValue().toString();
                name = ""+snapshot.child(Constant.KEY_NAME).getValue().toString();
                dp = ""+snapshot.child(Constant.KEY_PROFILE_IMAGE).getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            binding.postImageView.setImageBitmap(bitmap);
            binding.postImgMaterialView.setVisibility(View.VISIBLE);
            binding.postRl.setVisibility(View.GONE);

        }
    }

    private void uploadImg() {

        pd.setMessage("Uploading..");
        pd.show();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] finalImg = baos.toByteArray();
        final StorageReference filePath;
        filePath = storageReference.child("KEY_COMMUNITY_POSTS/").child(finalImg +"jpeg");
        final UploadTask uploadTask = filePath.putBytes(finalImg);
        uploadTask.addOnCompleteListener(AddCommunityPostActivity.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    DownloadUrl = String.valueOf(uri);
                                    uploadData();
                                }
                            });
                        }
                    });
                }else{
                    pd.dismiss();
                    uploadNoImageData();
                }
            }
        });

    }

    private void uploadNoImageData() {
        String TimeStamp= String.valueOf(System.currentTimeMillis());

        String title = binding.title.getEditText().getText().toString();
        String description = binding.description.getEditText().getText().toString();

        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put(Constant.KEY_UID,uid );
        hashMap.put(Constant.KEY_NAME, name );
        hashMap.put(Constant.KEY_EMAIL, email);
        hashMap.put(Constant.KEY_IMAGE, dp );
        hashMap.put(Constant.KEY_POST_LIKES, "0");
        hashMap.put(Constant.KEY_POST_COMMENTS, "0");
        hashMap.put(Constant.KEY_POST_ID, TimeStamp);
        hashMap.put(Constant.KEY_POST_TITLE, title);
        hashMap.put(Constant.KEY_POST_DESCRIPTION,description );
        hashMap.put(Constant.KEY_POST_IMAGE, Constant.KEY_NO_IMAGE);
        hashMap.put(Constant.KEY_POST_TIME, TimeStamp );

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_COMMUNITY_POSTS);
        reference.child(TimeStamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        pd.dismiss();
                        Toast.makeText(AddCommunityPostActivity.this, " Text Post Publish Successfully", Toast.LENGTH_SHORT).show();
                        binding.title.getEditText().setText(null);
                        binding.description.getEditText().setText(null);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(AddCommunityPostActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void uploadData() {

        String TimeStamp= String.valueOf(System.currentTimeMillis());

        String title = binding.title.getEditText().getText().toString();
        String description = binding.description.getEditText().getText().toString();

        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put(Constant.KEY_UID,uid );
        hashMap.put(Constant.KEY_NAME, name );
        hashMap.put(Constant.KEY_EMAIL, email);
        hashMap.put(Constant.KEY_IMAGE, dp );
        hashMap.put(Constant.KEY_POST_LIKES, "0");
        hashMap.put(Constant.KEY_POST_COMMENTS, "0");
        hashMap.put(Constant.KEY_POST_ID, TimeStamp);
        hashMap.put(Constant.KEY_POST_TITLE, title);
        hashMap.put(Constant.KEY_POST_DESCRIPTION,description );
        hashMap.put(Constant.KEY_POST_IMAGE, DownloadUrl);
        hashMap.put(Constant.KEY_POST_TIME, TimeStamp );

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_COMMUNITY_POSTS);
        reference.child(TimeStamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        pd.dismiss();
                        Toast.makeText(AddCommunityPostActivity.this, " Post Publish Successfully", Toast.LENGTH_SHORT).show();
                        binding.title.getEditText().setText(null);
                        binding.description.getEditText().setText(null);
                        binding.postImageView.setImageURI(null);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(AddCommunityPostActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                });

    }

}