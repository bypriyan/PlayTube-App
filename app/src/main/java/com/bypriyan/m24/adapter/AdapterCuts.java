package com.bypriyan.m24.adapter;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bypriyan.m24.R;
import com.bypriyan.m24.activity.SearchVideoActivity;
import com.bypriyan.m24.model.ModelComment;
import com.bypriyan.m24.model.ModelCuts;
import com.bypriyan.m24.utility.Constant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.annotations.NonNull;

public class AdapterCuts extends RecyclerView.Adapter<AdapterCuts.CutsViewPagerViewHolder> {


    private ArrayList<ModelCuts> cutsArrayList;
    private Context context;
    private boolean isLike = false;
    private int likes = 0;
    public AlertDialog alertDialog;
    private FirebaseUser user;
    private ArrayList<ModelComment> commentArrayList;
    private AdapterComment adapterComment;
    private ExecutorService executorService;

    public AdapterCuts(ArrayList<ModelCuts> cutsArrayList, Context context) {
        this.cutsArrayList = cutsArrayList;
        this.context = context;
        this.user = user = FirebaseAuth.getInstance().getCurrentUser();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @androidx.annotation.NonNull
    @Override
    public CutsViewPagerViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        return new CutsViewPagerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cuts_videos, parent, false));
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

        holder.videoView.setVideoPath(url);
        holder.titleTv.setText(title);
        holder.likeCountTv.setText(like);
        //like button
        holder.likeBtn.setOnClickListener(view -> {
            if(!isLike){
                // inc like
                likes  = Integer.parseInt(like);
                likes++;
                holder.likeCountTv.setText(""+likes);
                isLike = true;
                holder.likeIv.setImageResource(R.drawable.liked);
                updateLikeCount(likes, videoID);
            }else{
                // dec like
                likes--;
                holder.likeCountTv.setText(""+likes);
                isLike = false;
                holder.likeIv.setImageResource(R.drawable.ic_like);
                updateLikeCount(likes, videoID);
            }

        });

        loadChannelData(channelId ,videoUid,holder );

        holder.videoView.setOnPreparedListener(mediaPlayer -> {
            holder.progressbar.setVisibility(View.GONE);
            mediaPlayer.start();

            float videoRatio = mediaPlayer.getVideoWidth() / (float) mediaPlayer.getVideoHeight();
            float screenRatio = holder.videoView.getWidth() / (float) holder.videoView.getHeight();

            float scale = videoRatio / screenRatio;

            if (scale >= 1) {
                // Adjust the width to fill the screen width
                holder.videoView.setScaleX(1f);
                holder.videoView.setScaleY(1f / scale);
            } else {
                // Adjust the height to fill the screen height
                holder.videoView.setScaleX(scale);
                holder.videoView.setScaleY(1f);
            }

            // Center the video on the screen
            int videoViewWidth = (int) (holder.videoView.getWidth() * scale);
            int videoViewHeight = holder.videoView.getHeight();
            int horizontalPadding = (holder.videoView.getWidth() - videoViewWidth) / 2;
            holder.videoView.setPadding(horizontalPadding, 0, horizontalPadding, 0);
        });

        holder.videoView.setOnCompletionListener(mediaPlayer -> {
            mediaPlayer.start();
        });

        holder.searchBtn.setOnClickListener(view -> {
            context.startActivity(new Intent(context, SearchVideoActivity.class));
        });

        holder.commentBtn.setOnClickListener(view -> {
            openCommentBottomSheet(videoID);
        });

        holder.shareBtn.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.bypriyan.m24&hl=en");
            context.startActivity(Intent.createChooser(intent,"invite to M24 app"));
        });

        holder.backBtn.setOnClickListener(view -> {});

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                loadComments(videoID, holder);
            }
        });

    }

    private void loadComments(String videoID, CutsViewPagerViewHolder holder) {
        commentArrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_CUTS);
        reference.child(videoID).child(Constant.KEY_COMMENT).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                commentArrayList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelComment modelComment = ds.getValue(ModelComment.class);
                    commentArrayList.add(modelComment);
                }
                adapterComment = new AdapterComment(context, commentArrayList);
                holder.commentCountTv.setText(""+commentArrayList.size());
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });
    }

    private void openCommentBottomSheet(String videoID) {
        View view = LayoutInflater.from(context).inflate(R.layout.bottom_comments, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        ImageView close = view.findViewById(R.id.close);
        ImageView backBtn = view.findViewById(R.id.backBtn);
        RecyclerView recyclearComments = view.findViewById(R.id.recyclearComments);
        EditText commentEd = view.findViewById(R.id.commentEd);
        TextView applyBtn = view.findViewById(R.id.applyBtn);

        alertDialog = builder.create();
        recyclearComments.setAdapter(adapterComment);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        applyBtn.setOnClickListener(v -> {
            if (commentEd.getText().toString().isEmpty()) {
                commentEd.setError("Empty");
                commentEd.requestFocus();
            } else {
                String comment = commentEd.getText().toString().trim();
                uploadComment(comment, videoID, commentEd);
            }
        });

        alertDialog.show();
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        alertDialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void uploadComment(String comment, String videoID, EditText commentEd) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constant.KEY_COMMENT_TEXT, ""+comment);
        hashMap.put(Constant.KEY_COMMENT_TIMESTAMP, ""+timestamp);
        hashMap.put(Constant.KEY_COMMENT_USER_UID, ""+user.getUid());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_CUTS);
        reference.child(videoID).child(Constant.KEY_COMMENT).child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        commentEd.setText("");
                        alertDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateLikeCount(int likes, String videoID) {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constant.KEY_CUTS_VIDEO_LIKE, ""+likes);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_CUTS);
        reference.child(videoID).updateChildren(hashMap);
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
                            .centerInside().placeholder(R.drawable.ic_person).into(holderVideos.profileImage);
                    holderVideos.userName.setText("@"+channelName);

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

         TextView userName, titleTv, likeCountTv, commentCountTv;
         VideoView videoView;
         ProgressBar progressbar;

         ImageView searchBtn, moreImage, backBtn,likeIv;
         LinearLayout likeBtn, DislikeBtn, commentBtn, shareBtn;
         CircleImageView profileImage;

        CutsViewPagerViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            videoView = itemView.findViewById(R.id.videoView);

            userName = itemView.findViewById(R.id.userName);
            titleTv = itemView.findViewById(R.id.titleTv);
            likeCountTv = itemView.findViewById(R.id.likeCountTv);

            progressbar = itemView.findViewById(R.id.progressbar);
            profileImage = itemView.findViewById(R.id.profileImage);
            commentCountTv = itemView.findViewById(R.id.commentCountTv);

            likeBtn = itemView.findViewById(R.id.likeBtn);
            DislikeBtn = itemView.findViewById(R.id.DislikeBtn);
            commentBtn = itemView.findViewById(R.id.commentBtn);
            shareBtn = itemView.findViewById(R.id.shareBtn);

            searchBtn = itemView.findViewById(R.id.searchBtn);
            moreImage = itemView.findViewById(R.id.moreImage);
            backBtn = itemView.findViewById(R.id.backBtn);
            likeIv = itemView.findViewById(R.id.likeIv);

        }
    }

}
