package com.bypriyan.m24.ui.home;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bypriyan.m24.R;
import com.bypriyan.m24.adapter.AdapterChannel;
import com.bypriyan.m24.adapter.AdapterVideos;
import com.bypriyan.m24.databinding.FragmentHomeBinding;
import com.bypriyan.m24.model.ModelChannel;
import com.bypriyan.m24.model.ModelVideos;
import com.bypriyan.m24.utility.Constant;
import com.bypriyan.m24.viewModel.ChannelViewModel;
import com.bypriyan.m24.viewModel.VideosViewModel;
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

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ArrayList<ModelVideos> data;
    private VideosViewModel videosViewModel;
    private ExecutorService executorService;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        videosViewModel = new ViewModelProvider(getActivity()).get(VideosViewModel.class);
        executorService = Executors.newSingleThreadExecutor();
        context = getContext();

        isLoading(true);
        Log.d("TAG", "onCreateView: init fragment");

        videosViewModel.getData().observe(getViewLifecycleOwner(), newData -> {
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
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_CHANNEL_VIDEOS);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                data.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelVideos modelvideos = ds.getValue(ModelVideos.class);
                    data.add(modelvideos);
                }

                videosViewModel.setData(data);
                if(data.size()>0){
                    isLoading(false);
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

    private void showData(ArrayList<ModelVideos> data) {
        RecyclerView recyclerView = getView().findViewById(R.id.videosRV); // Replace with your RecyclerView's ID
        AdapterVideos adapter = new AdapterVideos(getContext(), data); // Replace with your adapter class
        recyclerView.setAdapter(adapter);
        if(data.size()>0){
            isLoading(false);
        }
    }
}