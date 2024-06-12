package com.bypriyan.m24.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bypriyan.m24.R;
import com.bypriyan.m24.model.ModelCuts;
import com.bypriyan.m24.utility.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.annotations.NonNull;

public class AdapterCutsVideos extends RecyclerView.Adapter<AdapterCutsVideos.CutsViewPagerViewHolder> {


    private ArrayList<ModelCuts> cutsArrayList;
    private Context context;

    public AdapterCutsVideos(ArrayList<ModelCuts> cutsArrayList, Context context) {
        this.cutsArrayList = cutsArrayList;
        this.context = context;
    }

    @androidx.annotation.NonNull
    @Override
    public CutsViewPagerViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        return new CutsViewPagerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_videos, parent, false));
    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull CutsViewPagerViewHolder holder, int position) {
        ModelCuts modelCuts = cutsArrayList.get(position);

        String description = modelCuts.getDescription();
        String like = modelCuts.getLike();
        String title = modelCuts.getTitle();
        String url = modelCuts.getUrl();
        String videoID = modelCuts.getVideoID();
        String videoTimestamp = modelCuts.getVideoTimestamp();
        String videoUid = modelCuts.getVideoUid();
        String views = modelCuts.getViews();
        String channelId = modelCuts.getVideoChannelId();

        String dateTime = convertToTimeAgoString(Long.parseLong(videoTimestamp));
        try {
            holder.titleTv.setText(title);
            holder.videoViewsTv.setText(views+" views");
            holder.videoTimeTv.setText(dateTime);

        }catch (Exception e){
        }

        try {
            extractVideoFrame(url, holder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Toast.makeText(context, ""+channelId+" "+videoUid, Toast.LENGTH_LONG).show();

        loadChannelData(channelId, videoUid, holder);

    }

    public static void extractVideoFrame(String videoUrl, CutsViewPagerViewHolder holderVideos) throws IOException {
        // Create a MediaMetadataRetriever
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        try {
            retriever.setDataSource(videoUrl, new HashMap<>());
            long timeUs = 1000000;
            Bitmap frame = retriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

            if (frame != null) {
                holderVideos.thumbnail.setImageBitmap(frame);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }

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
    private void loadChannelData(String channelId, String videoUid, CutsViewPagerViewHolder holderVideos) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_USERS)
                .child(videoUid).child(Constant.KEY_CHANNEL);
        Query query = reference.orderByChild(Constant.KEY_CHANNEL_ID).equalTo(channelId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
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
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return cutsArrayList.size();
    }

    public class CutsViewPagerViewHolder extends RecyclerView.ViewHolder{

        CircleImageView channelImg;
        TextView titleTv, channelNameTv,videoViewsTv,videoTimeTv;
        ImageView thumbnail;

        CutsViewPagerViewHolder(@NonNull @NotNull View itemView) {
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
