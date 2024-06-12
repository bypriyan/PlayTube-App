package com.bypriyan.m24.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bypriyan.m24.R;
import com.bypriyan.m24.adapter.AdapterVideos;
import com.bypriyan.m24.databinding.ActivitySearchVideoBinding;
import com.bypriyan.m24.databinding.ActivityUserProfileBinding;
import com.bypriyan.m24.model.ModelVideos;
import com.bypriyan.m24.utility.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchVideoActivity extends AppCompatActivity {

    private ActivitySearchVideoBinding binding;
    private ArrayList<ModelVideos> data;
    private AdapterVideos adapterVideos;
    boolean isLoad = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }
        getWindow().setStatusBarColor(ContextCompat.getColor(SearchVideoActivity.this, R.color.white));// set status background white
        binding = ActivitySearchVideoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                binding.searchView.clearFocus();
                Toast.makeText(SearchVideoActivity.this, ""+isLoad, Toast.LENGTH_SHORT).show();
                if(!isLoad){
                    loading(true);
                    loadProducts();
                }
                try{
                    adapterVideos.getFilter().filter(query.trim());
                }catch (Exception e){
                    e.printStackTrace();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    private void loadProducts() {
        data = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(Constant.KEY_CHANNEL_VIDEOS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                data.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelVideos modelVideos = ds.getValue(ModelVideos.class);
                    data.add(modelVideos);
                }
                Collections.reverse(data);
                adapterVideos = new AdapterVideos(SearchVideoActivity.this, data);
                binding.recyclearProducts.setAdapter(adapterVideos);

                // Log the data size to verify data retrieval
                Toast.makeText(SearchVideoActivity.this, ""+data.size(), Toast.LENGTH_SHORT).show();

                try {
                    adapterVideos.getFilter().filter(binding.searchView.getQuery().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                loading(false);
                isLoad = true;
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                loading(false);
            }
        });
    }


    private void loading(boolean isloading) {
        if (isloading) {
            binding.progressbar.setVisibility(View.VISIBLE);
        } else {
            binding.progressbar.setVisibility(View.GONE);
        }
    }

}