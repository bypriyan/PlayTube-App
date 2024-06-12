package com.bypriyan.m24.channelsActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentPagerAdapter;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bypriyan.m24.R;
import com.bypriyan.m24.adapter.AdapterChannel;
import com.bypriyan.m24.adapter.AdapterCutsVideos;
import com.bypriyan.m24.adapter.AdapterPost;
import com.bypriyan.m24.adapter.AdapterVideos;
import com.bypriyan.m24.adapter.VpAdapter;
import com.bypriyan.m24.channelsActivity.videos.CreatePostActivity;
import com.bypriyan.m24.channelsActivity.videos.UploadCutsActivity;
import com.bypriyan.m24.channelsActivity.videos.UploadVideoActivity;
import com.bypriyan.m24.databinding.ActivityMyChannelBinding;
import com.bypriyan.m24.model.ModelChannel;
import com.bypriyan.m24.model.ModelCuts;
import com.bypriyan.m24.model.ModelPost;
import com.bypriyan.m24.model.ModelVideos;
import com.bypriyan.m24.myChannelFrag.CutsChannelFragment;
import com.bypriyan.m24.myChannelFrag.PostsFragment;
import com.bypriyan.m24.myChannelFrag.VideosFragment;
import com.bypriyan.m24.utility.Constant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyChannelActivity extends AppCompatActivity {

    private ActivityMyChannelBinding binding;
    private String channelProfileImg, channelCoverImg, channelName, channelAbout, channelId, channelTimeStamp, channelUid;
    private ExecutorService executorService;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference;
    private ArrayList<ModelVideos> data;
    private ArrayList<ModelCuts> cutsArrayList;
    private ArrayList<ModelPost> postArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }
        getWindow().setStatusBarColor(ContextCompat.getColor(MyChannelActivity.this,R.color.white));// set status background white
        binding = ActivityMyChannelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        channelProfileImg = getIntent().getStringExtra(Constant.KEY_CHANNEL_PROFILE_IMAGE);
        channelCoverImg = getIntent().getStringExtra(Constant.KEY_CHANNEL_COVER_IMAGE);
        channelName = getIntent().getStringExtra(Constant.KEY_CHANNEL_NAME);
        channelAbout = getIntent().getStringExtra(Constant.KEY_CHANNEL_ABOUT);
        channelId = getIntent().getStringExtra(Constant.KEY_CHANNEL_ID);
        channelTimeStamp = getIntent().getStringExtra(Constant.KEY_CHANNEL_TIMESTAMP);
        channelUid = getIntent().getStringExtra(Constant.KEY_UID);
        executorService = Executors.newSingleThreadExecutor();
        firebaseAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_USERS);

        //setData
        setChannelData();
        loadVideosDataFromFirebase(firebaseAuth.getUid());

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                loadSubscribersCount(firebaseAuth.getCurrentUser().getUid());
            }
        });

        binding.backBtn.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.uploadBtn.setOnClickListener(view -> {
            showVideoUploadBottomSheet();
        });

        binding.moreBtn.setOnClickListener(view -> {
            openPopupMenu();
        });
    }

    private void openPopupMenu() {
        PopupMenu popupMenu = new PopupMenu(MyChannelActivity.this, binding.moreBtn, Gravity.END);

        popupMenu.getMenu().add(Menu.NONE, 0, 0 ,"videos");
        popupMenu.getMenu().add(Menu.NONE, 1 ,0 ,"cuts");
        popupMenu.getMenu().add(Menu.NONE, 2 ,0 ,"posts");

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int id = item.getItemId();
                if(id==0){
                    loadVideosDataFromFirebase(firebaseAuth.getUid());
                }
                else if(id==1){
                    loadCutsData(firebaseAuth.getUid());
                }else if(id==2){
                    loadPostData(firebaseAuth.getUid());
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void loadPostData(String uid) {
        postArrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_POST);
        reference.orderByChild(Constant.KEY_POST_UID).equalTo(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postArrayList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelPost modelPost = ds.getValue(ModelPost.class);
                    postArrayList.add(modelPost);
                }
                binding.recyclearRoomsVertical.setAdapter(new AdapterPost(MyChannelActivity.this, postArrayList));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void loadCutsData(String uid) {
        cutsArrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_CUTS);
        reference.orderByChild(Constant.KEY_CUTS_VIDEO_UID).equalTo(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cutsArrayList.clear();
                binding.channelVideosCount.setText(""+snapshot.getChildrenCount()+" videos");
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelCuts modelvideos = ds.getValue(ModelCuts.class);
                    cutsArrayList.add(modelvideos);
                }
                binding.recyclearRoomsVertical.setAdapter(new AdapterCutsVideos(cutsArrayList, MyChannelActivity.this));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void showVideoUploadBottomSheet() {
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_upload_video, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);

        ImageView close = view.findViewById(R.id.close);
        ImageView backBtn = view.findViewById(R.id.backBtn);
        RelativeLayout uploadVideoBtn = view.findViewById(R.id.uploadVideoBtn);
        RelativeLayout uploadCutsBtn = view.findViewById(R.id.uploadCutsBtn);
        RelativeLayout createPostBtn = view.findViewById(R.id.createPostBtn);

        AlertDialog  alertDialog = builder.create();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        uploadCutsBtn.setOnClickListener(view1 -> {
            Intent intent = new Intent(MyChannelActivity.this, UploadCutsActivity.class);
            intent.putExtra(Constant.KEY_VIDEO_CHANNEL_ID, channelId);
            startActivity(intent);
        });

        createPostBtn.setOnClickListener(view1 -> {
            Intent intent = new Intent(MyChannelActivity.this, CreatePostActivity.class);
            intent.putExtra(Constant.KEY_VIDEO_CHANNEL_ID, channelId);
            startActivity(intent);
        });

        uploadVideoBtn.setOnClickListener(view1 -> {
            Intent intent = new Intent(MyChannelActivity.this, UploadVideoActivity.class);
            intent.putExtra(Constant.KEY_VIDEO_CHANNEL_ID, channelId);
            startActivity(intent);
        });

        alertDialog.show();
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        alertDialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void loadVideosDataFromFirebase(String uid) {
        data = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_CHANNEL_VIDEOS);
        reference.orderByChild(Constant.KEY_VIDEO_UID).equalTo(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                data.clear();
                binding.channelVideosCount.setText(""+snapshot.getChildrenCount()+" videos");
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelVideos modelvideos = ds.getValue(ModelVideos.class);
                    data.add(modelvideos);
                }
                binding.recyclearRoomsVertical.setAdapter(new AdapterVideos(MyChannelActivity.this, data));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void loadSubscribersCount(String uid) {

        Query query = reference.child(uid).child(Constant.KEY_CHANNEL_SUBSCRIBERS);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String count = ""+snapshot.getChildrenCount();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.channelSubscribers.setText(count+" subscribers");
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setChannelData() {
        try {
            Glide.with(this).load(channelProfileImg)
                    .centerInside().placeholder(R.drawable.logo).into(binding.profileImageChannel);

            Glide.with(this).load(channelCoverImg)
                    .centerInside().placeholder(R.drawable.logo).into(binding.channelCoverImg);

            binding.channelName.setText(channelName);
            binding.channelNameToolBar.setText(channelName);

//            binding.channelNameToolBar.setText(channelName);
            if ((channelAbout.length() > 120)) {
                binding.channelAbout.setText(channelAbout.substring(0, 117) + "...");
            } else {
                binding.channelAbout.setText(channelAbout);
            }

        }catch (Exception e){
            Toast.makeText(this, ""+e, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}