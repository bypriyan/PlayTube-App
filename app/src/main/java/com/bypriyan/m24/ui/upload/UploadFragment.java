package com.bypriyan.m24.ui.upload;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bypriyan.m24.R;
import com.bypriyan.m24.channelsActivity.CreateChannelActivity;
import com.bypriyan.m24.adapter.AdapterChannel;
import com.bypriyan.m24.databinding.FragmentUploadBinding;
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

public class UploadFragment extends Fragment {

    private FragmentUploadBinding binding;
    private FirebaseUser firebaseUser;
    private ArrayList<ModelChannel> data;
    private ChannelViewModel channelViewModel;
    private ExecutorService executorService;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentUploadBinding.inflate(getLayoutInflater());
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        channelViewModel = new ViewModelProvider(getActivity()).get(ChannelViewModel.class);
        executorService = Executors.newSingleThreadExecutor();
        context = getContext();

        binding.createChannelButton.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), CreateChannelActivity.class));
        });

        isLoading(true);
        Log.d("TAG", "onCreateView: init fragment");

        channelViewModel.getData().observe(getViewLifecycleOwner(), newData -> {
            if (newData == null || newData.isEmpty()) {
                Log.d("TAG", "onCreateView: data nahi hai");
                    loadDataFromFirebase();
            } else {
                // Commented out: showData(newData);
                Log.d("TAG", "onCreateView: data hai");
                showData(newData);
            }
        });

        return binding.getRoot();
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

                channelViewModel.setData(data);
                        //
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
        RecyclerView recyclerView = getView().findViewById(R.id.channelRV); // Replace with your RecyclerView's ID
        AdapterChannel adapter = new AdapterChannel(getContext(), data); // Replace with your adapter class
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
}