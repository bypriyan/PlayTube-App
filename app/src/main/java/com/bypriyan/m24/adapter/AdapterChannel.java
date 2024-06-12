package com.bypriyan.m24.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bypriyan.m24.R;
import com.bypriyan.m24.channelsActivity.MyChannelActivity;
import com.bypriyan.m24.model.ModelChannel;
import com.bypriyan.m24.utility.Constant;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterChannel extends RecyclerView.Adapter<AdapterChannel.HolderChannel> {

    private Context context;
    private List<ModelChannel> channelArrayList;

    public AdapterChannel(Context context, List<ModelChannel> channelArrayList) {
        this.context = context;
        this.channelArrayList = channelArrayList;
    }

    @NonNull
    @Override
    public HolderChannel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_channel, parent, false);

        return new HolderChannel(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderChannel holder, int position) {

        ModelChannel modelChannel = channelArrayList.get(position);

        String channelProfileImage = modelChannel.getChannelProfileImage();
        String channelCoverImg = modelChannel.getChannelCoverImage();
        String channelName = modelChannel.getChannelName();
        String channelAbout = modelChannel.getChannelAbout();
        String channelId = modelChannel.getChannelId();
        String channelTimestamp = modelChannel.getChannelTimestamp();
        String uid = modelChannel.getUid();

        try {
            Glide.with(context).load(channelProfileImage)
                    .centerInside().placeholder(R.drawable.ic_person).into(holder.profileImageChannel);

            holder.channelName.setText(channelName);

            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(Long.parseLong(channelTimestamp));
            String dateTime = DateFormat.format("dd MMM yyyy", cal).toString();

            holder.creationDate.setText(dateTime);

        }catch (Exception e){
        }

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, MyChannelActivity.class);
            intent.putExtra(Constant.KEY_CHANNEL_PROFILE_IMAGE, channelProfileImage);
            intent.putExtra(Constant.KEY_CHANNEL_COVER_IMAGE, channelCoverImg);
            intent.putExtra(Constant.KEY_CHANNEL_NAME, channelName);
            intent.putExtra(Constant.KEY_CHANNEL_ABOUT, channelAbout);
            intent.putExtra(Constant.KEY_CHANNEL_ID, channelId);
            intent.putExtra(Constant.KEY_CHANNEL_TIMESTAMP, channelTimestamp);
            intent.putExtra(Constant.KEY_UID, uid);
            context.startActivity(intent);

        });

    }

    @Override
    public int getItemCount() {
        return channelArrayList.size();
    }

    class HolderChannel extends RecyclerView.ViewHolder{

        CircleImageView profileImageChannel;
        TextView channelName, creationDate;
        public HolderChannel(@NonNull View itemView) {
            super(itemView);
            profileImageChannel = itemView.findViewById(R.id.profileImageChannel);
            channelName = itemView.findViewById(R.id.channelName);
            creationDate = itemView.findViewById(R.id.creationDate);
        }
    }
}
