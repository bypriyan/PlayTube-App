package com.bypriyan.m24.ui.cuts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bypriyan.m24.R;
import com.bypriyan.m24.activity.CutsActivity;
import com.bypriyan.m24.activity.MainActivity;
import com.bypriyan.m24.adapter.AdapterCuts;
import com.bypriyan.m24.databinding.FragmentCutsBinding;
import com.bypriyan.m24.model.ModelCuts;
import com.bypriyan.m24.utility.Constant;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CutsFragment extends Fragment {

    private FragmentCutsBinding binding;
    private ArrayList<ModelCuts> modelCutsArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCutsBinding.inflate(getLayoutInflater());

        loadCuts();
//        startActivity(new Intent(getActivity(), CutsActivity.class));
        return binding.getRoot();
    }

    private void loadCuts() {
        modelCutsArrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(Constant.KEY_CUTS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelCutsArrayList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelCuts modelCuts = ds.getValue(ModelCuts.class);
                    modelCutsArrayList.add(modelCuts);
                }
                binding.viewPager.setAdapter(new AdapterCuts(modelCutsArrayList, getContext()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}