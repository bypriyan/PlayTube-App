package com.bypriyan.m24.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.bypriyan.m24.R;
import com.bypriyan.m24.adapter.AdapterCuts;
import com.bypriyan.m24.databinding.ActivityCutsBinding;
import com.bypriyan.m24.databinding.ActivityNotificationBinding;
import com.bypriyan.m24.model.ModelCuts;
import com.bypriyan.m24.ui.home.HomeFragment;
import com.bypriyan.m24.utility.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CutsActivity extends AppCompatActivity {

    private ActivityCutsBinding binding;
    private ArrayList<ModelCuts> modelCutsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }// set status background white

        binding = ActivityCutsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadCuts();

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
                binding.viewPager.setAdapter(new AdapterCuts(modelCutsArrayList, CutsActivity.this));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}