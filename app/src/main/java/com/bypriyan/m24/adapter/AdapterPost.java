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
import com.bypriyan.m24.model.ModelPost;
import com.bypriyan.m24.utility.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterPost extends RecyclerView.Adapter<AdapterPost.HolderPost> {

    private Context context;
    private ArrayList<ModelPost> postList;

    public AdapterPost(Context context, ArrayList<ModelPost> postList) {
        this.context = context;
        this.postList= postList;
    }

    @NonNull
    @Override
    public HolderPost onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_posts, parent, false);

        return new HolderPost(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPost holder, int position) {

        ModelPost modelPost = postList.get(position);

        String like = modelPost.getLike();
        String postID = modelPost.getPostID();
        String postTimeStamp = modelPost.getPostTimestamp();
        String postUid = modelPost.getPostUid();
        String title = modelPost.getTitle();
        String url = modelPost.getUrl();
        String videoChannelId = modelPost.getVideoChannelId();

        String dateTime = convertToTimeAgoString(Long.parseLong(postTimeStamp));

        try {
            Glide.with(context).load(url)
                    .centerInside().placeholder(R.drawable.ic_person).into(holder.postImage);
            holder.pTime.setText(dateTime);
            holder.pTitle.setText(title);

        }catch (Exception e){
        }

        loadChannelData(videoChannelId, postUid, holder);

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
    private void loadChannelData(String channelId, String videoUid, HolderPost holderVideos) {
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
                            .centerInside().placeholder(R.drawable.ic_person).into(holderVideos.profileImage);
                    holderVideos.channelName.setText(channelName);

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
        return postList.size();
    }

    class HolderPost extends RecyclerView.ViewHolder{

        CircleImageView profileImage;
        TextView channelName, pTime, pTitle;
        ImageView postImage;
        public HolderPost(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.pProfilePic);
            channelName = itemView.findViewById(R.id.channelNam);
            pTime = itemView.findViewById(R.id.pTime);
            pTitle = itemView.findViewById(R.id.pTitle);
            postImage = itemView.findViewById(R.id.pImage);
        }
    }
}
