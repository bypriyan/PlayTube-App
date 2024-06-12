package com.bypriyan.m24.channelsActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bypriyan.m24.R;
import com.bypriyan.m24.adapter.AdapterChannel;
import com.bypriyan.m24.databinding.ActivityAllChannelBinding;
import com.bypriyan.m24.model.ModelChannel;
import com.bypriyan.m24.viewModel.ChannelViewModel;
import com.bypriyan.m24.utility.Constant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AllChannelActivity extends AppCompatActivity {

    private ActivityAllChannelBinding binding;
    private ExecutorService executorService;
    private ChannelViewModel channelViewModel;
    private ArrayList<ModelChannel> data;
    private FirebaseUser firebaseUser;

    private RecyclerView recyclerView;
    private AdapterChannel adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }
        getWindow().setStatusBarColor(ContextCompat.getColor(AllChannelActivity.this,R.color.white));// set status background white
        binding = ActivityAllChannelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //init
        executorService = Executors.newSingleThreadExecutor();
        channelViewModel = new ViewModelProvider(this).get(ChannelViewModel.class);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        binding.backBtn.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.createBtn.setOnClickListener(view -> {
            startActivity(new Intent(AllChannelActivity.this, CreateChannelActivity.class));
        });

        binding.createChannelButton.setOnClickListener(view -> {
            startActivity(new Intent(AllChannelActivity.this, CreateChannelActivity.class));
        });

        isLoading(true);

        channelViewModel.getData().observe(this, newData -> {
            if (newData == null || newData.isEmpty()) {
                Log.d("TAG", "onCreateView: data not avi");
                executorService.submit(() -> {
                    loadDataFromFirebase();
                });
            } else {
                // Commented out: showData(newData);
                Log.d("TAG", "onCreateView: data avi");
                showData(newData);
            }
        });

    }

    private void loadDataFromFirebase() {
        data = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_USERS);
        reference.child(firebaseUser.getUid()).child(Constant.KEY_CHANNEL).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                data.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelChannel modelChannel = ds.getValue(ModelChannel.class);
                    data.add(modelChannel);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("TAG", "onDataChange: bhai mai hit hua hu ");
                        channelViewModel.setData(data);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                isLoading(false);
            }
        });
    }
    private void isLoading(boolean s){
        if(s){
            binding.progressbar.setVisibility(View.VISIBLE);
        }else{
            binding.progressbar.setVisibility(View.GONE);
        }
    }

    private void showData(List<ModelChannel> data) {
         recyclerView = findViewById(R.id.channelRV); // Replace with your RecyclerView's ID
         adapter = new AdapterChannel(AllChannelActivity.this, data); // Replace with your adapter class
        recyclerView.setAdapter(adapter);
        if(data.size()>0){
            isLoading(false);
            binding.channelRV.setVisibility(View.VISIBLE);
            binding.lottieAnimationLinear.setVisibility(View.GONE);
        }else{
            isLoading(false);
            binding.channelRV.setVisibility(View.GONE);
            binding.lottieAnimationLinear.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}