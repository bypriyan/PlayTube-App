package com.bypriyan.m24.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bypriyan.m24.R;
import com.bypriyan.m24.model.ModelVideos;
import com.bypriyan.m24.utility.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterHistoryVideo extends RecyclerView.Adapter<AdapterHistoryVideo.HolderVideosHistory> {

    private Context context;
    private ArrayList<String> videosList;

    public AdapterHistoryVideo(Context context, ArrayList<String> videosList) {
        this.context = context;
        this.videosList= videosList;
    }

    @NonNull
    @Override
    public HolderVideosHistory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_history_video, parent, false);

        return new HolderVideosHistory(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderVideosHistory holder, int position) {

        String videoId = videosList.get(position);
        loadVideoData(videoId, holder);

        holder.itemView.setOnClickListener(view -> {
            Toast.makeText(context, "video will play", Toast.LENGTH_SHORT).show();
        });

    }

    private void loadVideoData(String videoId, HolderVideosHistory holderVideosHistory) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_CHANNEL_VIDEOS);
        Query query = reference.orderByChild(Constant.KEY_CUTS_VIDEO_ID).equalTo(videoId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String thumbnailImg ="", title= "";
                for(DataSnapshot ds: snapshot.getChildren()){
                     thumbnailImg =""+ds.child(Constant.KEY_VIDEO_thumbnail).getValue();
                     title = ""+ds.child(Constant.KEY_VIDEO_TITLE).getValue();
                }
                try {
                    Glide.with(context).load(thumbnailImg)
                            .centerInside().placeholder(R.drawable.ic_person).into(holderVideosHistory.thumbnailImageView);
                    holderVideosHistory.titleImageTv.setText(title);

                }catch (Exception e){
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return videosList.size();
    }

    class HolderVideosHistory extends RecyclerView.ViewHolder{

        TextView titleImageTv;
        ImageView thumbnailImageView;
        public HolderVideosHistory(@NonNull View itemView) {
            super(itemView);
            thumbnailImageView = itemView.findViewById(R.id.thumbnailImageView);
            titleImageTv = itemView.findViewById(R.id.titleTv);
        }
    }
}
