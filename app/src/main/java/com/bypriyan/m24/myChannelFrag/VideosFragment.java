package com.bypriyan.m24.myChannelFrag;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bypriyan.m24.R;
import com.bypriyan.m24.adapter.AdapterVideos;
import com.bypriyan.m24.channelsActivity.MyChannelActivity;
import com.bypriyan.m24.databinding.FragmentCutsBinding;
import com.bypriyan.m24.databinding.FragmentVideosBinding;
import com.bypriyan.m24.model.ModelVideos;
import com.bypriyan.m24.utility.Constant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class VideosFragment extends Fragment {

    private FragmentVideosBinding binding;
    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelVideos> data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentVideosBinding.inflate(getLayoutInflater());
        firebaseAuth = FirebaseAuth.getInstance();

        loadDataFromFirebase(firebaseAuth.getUid());
        return binding.getRoot();
    }

    private void loadDataFromFirebase(String uid) {
        data = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_CHANNEL_VIDEOS);
        reference.orderByChild(Constant.KEY_VIDEO_UID).equalTo(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                data.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelVideos modelvideos = ds.getValue(ModelVideos.class);
                    data.add(modelvideos);
                }
                binding.recyclearVideos.setAdapter(new AdapterVideos(getContext(), data));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}