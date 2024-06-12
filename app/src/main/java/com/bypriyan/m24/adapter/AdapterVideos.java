package com.bypriyan.m24.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bypriyan.m24.R;
import com.bypriyan.m24.filter.FilterSearchVideo;
import com.bypriyan.m24.model.ModelVideos;
import com.bypriyan.m24.utility.Constant;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterVideos extends RecyclerView.Adapter<AdapterVideos.HolderVideos> implements Filterable {

    public Context context;
    public ArrayList<ModelVideos> videosList;
    public ArrayList<ModelVideos> videosArrayListFilter;
    private FilterSearchVideo filter;
    FirebaseAuth firebaseAuth;
    String myUid="";
    int count=0;

    public AdapterVideos(Context context, ArrayList<ModelVideos> videosList) {
        this.context = context;
        this.videosList= videosList;
        this.videosArrayListFilter= videosList;
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.myUid = firebaseAuth.getUid();

    }

    @NonNull
    @Override
    public HolderVideos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_videos, parent, false);

        return new HolderVideos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderVideos holder, int position) {

        ModelVideos modelVideos = videosList.get(position);

        String description = modelVideos.getDescription();
        String like = modelVideos.getLike();
        String title = modelVideos.getTitle();
        String url = modelVideos.getUrl();
        String videoID = modelVideos.getVideoID();
        String videoThumbnail = modelVideos.getVideoThumbnail();
        String videoTimestamp = modelVideos.getVideoTimestamp();
        String videoUid = modelVideos.getVideoUid();
        String views = modelVideos.getViews();
        String channelId = modelVideos.getVideoChannelId();

        String dateTime = convertToTimeAgoString(Long.parseLong(videoTimestamp));

        try {
            Glide.with(context).load(videoThumbnail)
                    .centerInside().placeholder(R.drawable.ic_person).into(holder.thumbnail);
            holder.titleTv.setText(title);
            holder.videoViewsTv.setText(views+" views");
            holder.videoTimeTv.setText(dateTime);

        }catch (Exception e){
        }

        loadChannelData(channelId, videoUid, holder);
        loadViews(channelId, videoID, holder);

        holder.itemView.setOnClickListener(view -> {
            sendToHistory(myUid, videoID,videoUid,channelId);
        });

    }

    private void loadViews(String channelId, String videoID, HolderVideos holder) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_VIEWS);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                count = 0;
                for(DataSnapshot ds: snapshot.getChildren()){
                    String channelIdView = ""+ds.child(Constant.KEY_CHANNEL_ID).getValue();

                    if(channelIdView.equals(channelId)){
                        count++;
                    }
                }

                holder.videoViewsTv.setText(""+count+" views");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendToHistory(String videoUID, String videoId, String videoUid, String channelId) {

        HashMap<String , String> hashMap = new HashMap<>();
        hashMap.put(Constant.KEY_CUTS_VIDEO_ID, videoId);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_USERS);
        reference.child(videoUID).child(Constant.KEY_VIDEO_HISTORY).push().setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                sendView(videoId,myUid, videoUid,channelId);
            }
        });
    }

    private void sendView(String videoId, String myUid, String videoUID, String channelId) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        HashMap<String, Object> hashMap =  new HashMap<>();
        hashMap.put(Constant.KEY_UID, myUid);
        hashMap.put(Constant.KEY_TIMESTAMP, timestamp);
        hashMap.put(Constant.KEY_VIDEO_ID, videoId);
        hashMap.put(Constant.KEY_VIDEO_UID, videoUID);
        hashMap.put(Constant.KEY_CHANNEL_ID, channelId);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_VIEWS);
        reference.child(timestamp).setValue(hashMap);

    }

    public static String convertToTimeAgoString(long timeMillis) {
        long currentTimeMillis = System.currentTimeMillis();
        long differenceMillis = currentTimeMillis - timeMillis;

        if (differenceMillis < 0) {
            return "In the future"; // Handle future timestamps
        }

        long seconds = TimeUnit.MILLISECONDS.toSeconds(differenceMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(differenceMillis);
        long hours = TimeUnit.MILLISECONDS.toHours(differenceMillis);
        long days = TimeUnit.MILLISECONDS.toDays(differenceMillis);
        long months = days / 30; // Approximate number of months (assuming 30 days in a month)
        long years = months / 12; // Approximate number of years (assuming 12 months in a year)

        if (years > 0) {
            return years + (years == 1 ? " year ago" : " years ago");
        } else if (months > 0) {
            return months + (months == 1 ? " month ago" : " months ago");
        } else if (days > 0) {
            return days + (days == 1 ? " day ago" : " days ago");
        } else if (hours > 0) {
            return hours + (hours == 1 ? " hour ago" : " hours ago");
        } else if (minutes > 0) {
            return minutes + (minutes == 1 ? " minute ago" : " minutes ago");
        } else {
            return seconds + (seconds == 1 ? " second ago" : " seconds ago");
        }
    }
    private void loadChannelData(String channelId, String videoUid, HolderVideos holderVideos) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_USERS)
                .child(videoUid).child(Constant.KEY_CHANNEL);
        Query query = reference.orderByChild(Constant.KEY_CHANNEL_ID).equalTo(channelId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String channelImg ="", channelName= "";
                for(DataSnapshot ds: snapshot.getChildren()){
                     channelImg =""+ds.child(Constant.KEY_CHANNEL_PROFILE_IMAGE).getValue();
                     channelName = ""+ds.child(Constant.KEY_CHANNEL_NAME).getValue();
                }
                try {
                    Glide.with(context).load(channelImg)
                            .centerInside().placeholder(R.drawable.ic_person).into(holderVideos.channelImg);
                    holderVideos.channelNameTv.setText(channelName);

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

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new FilterSearchVideo(this, videosArrayListFilter);
        }
        return filter;
    }


    class HolderVideos extends RecyclerView.ViewHolder{

        CircleImageView channelImg;
        TextView titleTv, channelNameTv,videoViewsTv,videoTimeTv;
        ImageView thumbnail;
        public HolderVideos(@NonNull View itemView) {
            super(itemView);
            channelImg = itemView.findViewById(R.id.channelImg);
            titleTv = itemView.findViewById(R.id.titleTv);
            channelNameTv = itemView.findViewById(R.id.channelNameTv);
            videoViewsTv = itemView.findViewById(R.id.videoViewsTv);
            videoTimeTv = itemView.findViewById(R.id.videoTimeTv);
            thumbnail = itemView.findViewById(R.id.thumbnail);
        }
    }
}
