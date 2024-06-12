package com.bypriyan.m24.ui.library;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bypriyan.m24.activity.HelpAndFeedbackActivity;
import com.bypriyan.m24.activity.NotificationActivity;
import com.bypriyan.m24.activity.SupportActivity;
import com.bypriyan.m24.activity.UserProfileActivity;
import com.bypriyan.m24.adapter.AdapterCutsVideos;
import com.bypriyan.m24.adapter.AdapterHistoryVideo;
import com.bypriyan.m24.channelsActivity.AllChannelActivity;
import com.bypriyan.m24.channelsActivity.CreateChannelActivity;
import com.bypriyan.m24.channelsActivity.MyChannelActivity;
import com.bypriyan.m24.databinding.FragmentLibraryBinding;
import com.bypriyan.m24.model.ModelCuts;
import com.bypriyan.m24.utility.Constant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.guieffect.qual.UI;

import java.util.ArrayList;

public class LibraryFragment extends Fragment {

    private FragmentLibraryBinding binding;
    private ArrayList<String> videoHistoryList;
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLibraryBinding.inflate(getLayoutInflater());

        //init
        firebaseAuth = FirebaseAuth.getInstance();

        loadVideoHistoryData(firebaseAuth.getUid().toString());

        binding.channelBtn.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), AllChannelActivity.class));
        });

        binding.createChannelBtn.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), CreateChannelActivity.class));
        });

        binding.notificationBtn.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), NotificationActivity.class));
        });

        binding.updateBtn.setOnClickListener(view -> {
            Toast.makeText(getContext(), "your app is up to date..", Toast.LENGTH_SHORT).show();
        });

        binding.feedBackBtn.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), HelpAndFeedbackActivity.class));
        });

        binding.helpBtn.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), SupportActivity.class));
        });

        return binding.getRoot();
    }

    private void loadVideoHistoryData(String uid) {
        videoHistoryList= new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_USERS);
        reference.child(uid).child(Constant.KEY_VIDEO_HISTORY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                videoHistoryList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    String id = ""+ds.child(Constant.KEY_CUTS_VIDEO_ID).getValue();
                    videoHistoryList.add(id);
                }
                binding.recyclearVideosHistory.setAdapter(new AdapterHistoryVideo( getContext(), videoHistoryList));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}