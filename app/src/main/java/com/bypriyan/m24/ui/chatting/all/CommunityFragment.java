package com.bypriyan.m24.ui.chatting.all;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bypriyan.m24.R;
import com.bypriyan.m24.adapter.AdapterCommunityPost;
import com.bypriyan.m24.databinding.FragmentCommunityBinding;
import com.bypriyan.m24.model.ModelCommunityPost;
import com.bypriyan.m24.utility.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class CommunityFragment extends Fragment {

    ArrayList<ModelCommunityPost> postList;
    private FragmentCommunityBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCommunityBinding.inflate(getLayoutInflater());

        postList = new ArrayList<>();

        loadPost();

        return binding.getRoot();
    }

    private void loadPost() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_COMMUNITY_POSTS);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                postList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    ModelCommunityPost modelPost = ds.getValue(ModelCommunityPost.class);
                    postList.add(modelPost);
                }
                binding.videosRV.setAdapter(new AdapterCommunityPost(getContext(), postList));
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                Toast.makeText(getActivity(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}