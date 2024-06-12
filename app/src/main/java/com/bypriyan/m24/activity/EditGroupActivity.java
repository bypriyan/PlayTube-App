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
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bypriyan.m24.R;
import com.bypriyan.m24.databinding.ActivityEditGroupBinding;
import com.bypriyan.m24.utility.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class EditGroupActivity extends AppCompatActivity {

    private ActivityEditGroupBinding binding;
    String groupId, groupIcons, groupTitles, groupDescriptions;
    FirebaseAuth firebaseAuth;
    private final int REQ = 1;
    private Bitmap bitmap;
    String DownloadUrl="";
    private ProgressDialog pd;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.appColor));
        binding = ActivityEditGroupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        groupId = getIntent().getStringExtra(Constant.KEY_GROUP_ID);
        DownloadUrl = getIntent().getStringExtra(Constant.KEY_GROUP_ICON);
        groupTitles = getIntent().getStringExtra(Constant.KEY_GROUP_TITLE);
        groupDescriptions = getIntent().getStringExtra(Constant.KEY_GROUP_DESCRIPTION);

        setInfo();



        pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);

        storageReference = FirebaseStorage.getInstance().getReference();


        binding.profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        binding.postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.groupName.getEditText().getText().toString().isEmpty()){
                    binding.groupName.setError("Empty");
                    binding.groupName.requestFocus();
                }else if(binding.description.getEditText().getText().toString().isEmpty()){
                    binding.description.setError("Empty");
                    binding.description.requestFocus();
                }else if(bitmap==null){
                    uploadData();

                }else{
                    uploadImg();
                }
            }
        });

    }

    private void setInfo() {

        try{
            Glide
                    .with(EditGroupActivity.this)
                    .load(DownloadUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_person)
                    .into(binding.profileImageView);
        }catch (Exception e){

        }

        binding.groupName.getEditText().setText(groupTitles);
        binding.description.getEditText().setText(groupDescriptions);


    }

    private void uploadData() {
        pd.setMessage("Updating group info..");
        pd.show();
        String TimeStamp= String.valueOf(System.currentTimeMillis());

        String title = binding.groupName.getEditText().getText().toString();
        String description = binding.description.getEditText().getText().toString();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constant.KEY_GROUP_TITLE, title );
        hashMap.put(Constant.KEY_GROUP_DESCRIPTION, description);
        hashMap.put(Constant.KEY_GROUP_ICON, DownloadUrl );

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_GROUPS);
        reference.child(groupId).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        pd.dismiss();
                        Toast.makeText(EditGroupActivity.this, "Group info updated...", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(EditGroupActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void uploadImg() {

        pd.setMessage("Updating group info..");
        pd.show();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] finalImg = baos.toByteArray();
        final StorageReference filePath;
        filePath = storageReference.child("Group_Imgs/").child(finalImg +"jpeg");
        final UploadTask uploadTask = filePath.putBytes(finalImg);
        uploadTask.addOnCompleteListener(EditGroupActivity.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
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
                }
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
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            binding.profileImageView.setImageBitmap(bitmap);
        }
    }

}