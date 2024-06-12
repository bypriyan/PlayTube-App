package com.bypriyan.m24.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.Toast;

import com.bypriyan.m24.R;
import com.bypriyan.m24.databinding.ActivityAnalyticsBinding;
import com.bypriyan.m24.model.ModelUsers;
import com.bypriyan.m24.model.ModelViews;
import com.bypriyan.m24.utility.Constant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Locale;

public class AnalyticsActivity extends AppCompatActivity {

    private ActivityAnalyticsBinding binding;
    private String lastSevenTimenstamp, last30DayTimeStamp;
    private FirebaseAuth firebaseAuth;
    private  int viewsLastSeven = 0, viewLast30 =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.appColor));
        binding = ActivityAnalyticsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        lastSevenTimenstamp = String.valueOf((System.currentTimeMillis()-604800000));
        long l = System.currentTimeMillis() - 2592000000L;
        last30DayTimeStamp = String.valueOf(l);
        firebaseAuth = FirebaseAuth.getInstance();

        loadLastSevenDaysView(lastSevenTimenstamp);
        load30DaysView(last30DayTimeStamp);

        binding.backBtn.setOnClickListener(view -> {
            onBackPressed();
        });

    }

    private void loadLastSevenDaysView(String lastSevenTimenstamp) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_VIEWS);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                viewsLastSeven=0;
                long timeStampOfLastSeven = Long.parseLong(lastSevenTimenstamp);
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelViews modelUsers = ds.getValue(ModelViews.class);
                    String uid = modelUsers.getVideoUid();
                    String viewTimeStampe = modelUsers.getTimestamp();
                    long viewTimeStamp = Long.parseLong(modelUsers.getTimestamp());
                    if(uid.equals(firebaseAuth.getUid())&&viewTimeStamp>timeStampOfLastSeven){
                        viewsLastSeven++;
                    }
                }

                binding.last7dayViews.setText("views in last seven days : "+viewsLastSeven);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void load30DaysView(String last30DayTimeStamp) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_VIEWS);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                viewLast30=0;
                long timeStampOfLast30 = Long.parseLong(last30DayTimeStamp);
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelViews modelUsers = ds.getValue(ModelViews.class);
                    String uid = modelUsers.getVideoUid();
                    String viewTimeStampe = modelUsers.getTimestamp();
                    long viewTimeStamp = Long.parseLong(modelUsers.getTimestamp());
                    if(uid.equals(firebaseAuth.getUid())&&viewTimeStamp>timeStampOfLast30){
                        viewLast30++;
                    }
                }

                binding.last30dayViews.setText("views in last 30 days : "+viewLast30);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



}