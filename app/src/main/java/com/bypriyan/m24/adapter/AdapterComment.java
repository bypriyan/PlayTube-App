package com.bypriyan.m24.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bypriyan.m24.R;
import com.bypriyan.m24.channelsActivity.MyChannelActivity;
import com.bypriyan.m24.model.ModelChannel;
import com.bypriyan.m24.model.ModelComment;
import com.bypriyan.m24.utility.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterComment extends RecyclerView.Adapter<AdapterComment.HolderComment> {

    private Context context;
    private List<ModelComment> commentList;

    public AdapterComment(Context context, List<ModelComment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public HolderComment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_comments, parent, false);

        return new HolderComment(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderComment holder, int position) {

        ModelComment modelComment = commentList.get(position);

        String commentText = modelComment.getCommentText();
        String timestamp = modelComment.getTimestamp();
        String userUid = modelComment.getCommentUID();

        try {
            holder.commentText.setText(commentText);

            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(Long.parseLong(timestamp));
            String dateTime = DateFormat.format("dd MMM yyyy", cal).toString();

            holder.commentDate.setText(dateTime);

            loadProfileImage(userUid, holder);

        }catch (Exception e){
        }

    }

    private void loadProfileImage(String userUid, HolderComment holderComment) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_USERS);
        reference.child(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String imgUrl = ""+snapshot.child(Constant.KEY_PROFILE_IMAGE).getValue();
                Glide.with(context).load(imgUrl).centerInside().placeholder(R.drawable.ic_person).into(holderComment.profileImageUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    class HolderComment extends RecyclerView.ViewHolder{

        CircleImageView profileImageUser;
        TextView commentText, commentDate;
        public HolderComment(@NonNull View itemView) {
            super(itemView);
            profileImageUser = itemView.findViewById(R.id.profileImageUser);
            commentText = itemView.findViewById(R.id.commentText);
            commentDate = itemView.findViewById(R.id.commentDate);
        }
    }
}
