package com.bypriyan.m24.ui.chatting.all;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bypriyan.m24.R;
import com.bypriyan.m24.adapter.AdapterGroupChat;
import com.bypriyan.m24.databinding.FragmentCommunityBinding;
import com.bypriyan.m24.databinding.FragmentGroupChattingBinding;
import com.bypriyan.m24.model.ModelCommunityPost;
import com.bypriyan.m24.model.ModelGroup;
import com.bypriyan.m24.utility.Constant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class GroupChattingFragment extends Fragment {

    ArrayList<ModelGroup> groupArrayList;
    private FragmentGroupChattingBinding binding;
    FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentGroupChattingBinding.inflate(getLayoutInflater());

        groupArrayList = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();

        loadGroupChatList();

        return binding.getRoot();
    }

    private void loadGroupChatList() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_GROUPS);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                groupArrayList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    if(ds.child(Constant.KEY_PARTICIPENTS).child(firebaseAuth.getUid()).exists()){
                        ModelGroup modelGroup = ds.getValue(ModelGroup.class);
                        groupArrayList.add(modelGroup);
                    }
                    binding.groupRV.setAdapter(new AdapterGroupChat(getActivity(), groupArrayList));
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

}