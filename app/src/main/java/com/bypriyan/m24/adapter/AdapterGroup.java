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
import com.bypriyan.m24.activity.GroupChatActivity;
import com.bypriyan.m24.model.ModelGroup;
import com.bypriyan.m24.utility.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterGroup extends RecyclerView.Adapter<AdapterGroup.MyHolder> {

    Context context;
    List<ModelGroup> groupChatList;

    public AdapterGroup(Context context, List<ModelGroup> groupChatList) {
        this.context = context;
        this.groupChatList = groupChatList;
    }

    @NonNull
    @NotNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_groupchat_list, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterGroup.MyHolder holder, int position) {

        ModelGroup model = groupChatList.get(position);

        String groupId = model.getGroupId();
        String groupIcon = model.getGrooupIcon();
        String groupName = model.getGrooupTitle();

        holder.senderName.setText("");
        holder.timeTv.setText("");
        holder.messageGroup.setText("");

        loadLastMessage(model, holder);

        holder.groupName.setText(groupName);

        try{
            Glide
                    .with(context)
                    .load(groupIcon)
                    .centerCrop()
                    .placeholder(R.drawable.ic_group_icon)
                    .into(holder.groupProfile);

        }catch (Exception e){

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, GroupChatActivity.class);
                intent.putExtra("groupId", groupId);
                intent.putExtra("groupIcon", groupIcon);
                intent.putExtra("groupName", groupName);
                context.startActivity(intent);
            }
        });



    }

    private void loadLastMessage(ModelGroup model, MyHolder holder) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_GROUPS);
        reference.child(model.getGroupId()).child(Constant.KEY_MESSAGE).limitToLast(1)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                        for(DataSnapshot ds: snapshot.getChildren()){
                            String message = ""+ds.child(Constant.KEY_MESSAGE).getValue();
                            String timeStamp = ""+ds.child(Constant.KEY_TIMESTAMP).getValue();
                            String sender = ""+ds.child(Constant.KEY_SENDER).getValue();
                            String messageType = ""+ds.child("type").getValue();

                            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                            cal.setTimeInMillis(Long.parseLong(timeStamp));
                            String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();

                            if(messageType.equals("image")){
                                holder.messageGroup.setText("send photo");
                            }else{
                                holder.messageGroup.setText(message);
                            }

                            holder.timeTv.setText(dateTime);


                            DatabaseReference reference1= FirebaseDatabase.getInstance().getReference(Constant.KEY_USERS);
                            reference1.orderByChild(Constant.KEY_UID).equalTo(sender)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                            for(DataSnapshot ds: snapshot.getChildren()){
                                                String name = ""+ds.child(Constant.KEY_NAME).getValue();

                                                holder.senderName.setText(name);

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                        }
                                    });


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

    }

    @Override
    public int getItemCount() {
        return groupChatList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        CircleImageView groupProfile;
        TextView groupName, senderName, messageGroup, timeTv;

        public MyHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            groupProfile = itemView.findViewById(R.id.groupProfile);

            groupName = itemView.findViewById(R.id.groupName);
            senderName = itemView.findViewById(R.id.senderName);
            messageGroup = itemView.findViewById(R.id.messageGroup);
            timeTv = itemView.findViewById(R.id.timeTv);

        }
    }
}
