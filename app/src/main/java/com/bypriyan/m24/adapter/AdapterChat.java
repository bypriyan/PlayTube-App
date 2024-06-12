package com.bypriyan.m24.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bypriyan.m24.R;
import com.bypriyan.m24.model.ModelChat;
import com.bypriyan.m24.utility.Constant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder> {

    Context context;
    List<ModelChat> chatList;
    private static final int MSG_TYPE_LEFT= 0;
    private static final int MSG_TYPE_RIGHT= 1;
    String imageUrl;

    FirebaseUser firebaseUser;

    public AdapterChat(Context context, List<ModelChat> chatList, String imageUrl) {
        this.context = context;
        this.chatList = chatList;
        this.imageUrl = imageUrl;
    }

    @NotNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        if(i==MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, parent, false);
            return new MyHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_left, parent, false);
            return new MyHolder(view);
        }

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String message = chatList.get(position).getMessage();
        String timestamp = chatList.get(position).getTimestamp();
        String type = chatList.get(position).getType();


        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();

        if(type.equals("text")){
            holder.message.setVisibility(View.VISIBLE);
            holder.messageIv.setVisibility(View.GONE);

            holder.message.setText(message);
        }else{
            holder.message.setVisibility(View.GONE);
            holder.messageIv.setVisibility(View.VISIBLE);

            try{
                Glide
                        .with(context)
                        .load(message)
                        .centerCrop()
                        .placeholder(R.drawable.ic_image)
                        .into(holder.messageIv);
            }catch (Exception e){

            }

            holder.messageIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Intent intent = new Intent(context, FullimageView.class);
//                    intent.putExtra("image", message);
//                    context.startActivity(intent);
                }
            });

        }

        holder.message.setText(message);
        holder.time.setText(dateTime);

        Glide
                .with(context)
                .load(imageUrl)
                .centerCrop()
                .placeholder(R.drawable.ic_person)
                .into(holder.profileImg);

        holder.messageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure to delete this message?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DeleteMessage(position);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
            }
        });

        holder.messageLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
//

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure to delete this message?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DeleteMessage(position);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();

                //

                return false;
            }
        });

        if(position== chatList.size()-1){
            if(chatList.get(position).isSeen()){
                holder.isSeen.setText("Seen");
            }else{
                holder.isSeen.setText("Delivered");
            }
        }else{
            holder.isSeen.setVisibility(View.GONE);
        }


    }

    private void DeleteMessage(int position) {

        String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String messageTimeStamp = chatList.get(position).getTimestamp();
        DatabaseReference  reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_CHATS);
        Query query = reference.orderByChild(Constant.KEY_TIMESTAMP).equalTo(messageTimeStamp);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){

                    if(ds.child(Constant.KEY_SENDER).getValue().equals(myUid)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put(Constant.KEY_MESSAGE,"This message was deleted....");
                        ds.getRef().updateChildren(hashMap);

                        Toast.makeText(context, "Message deleted..", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context, "You Can delete only your message..", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }


    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(chatList.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }

    class MyHolder extends RecyclerView.ViewHolder{

        CircleImageView profileImg;
        TextView message, time, isSeen;
        LinearLayout messageLayout;
        ImageView messageIv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            isSeen = itemView.findViewById(R.id.isSeen);
            profileImg = itemView.findViewById(R.id.profileImg);
            message = itemView.findViewById(R.id.messageTv);
            time = itemView.findViewById(R.id.time);
            messageLayout = itemView.findViewById(R.id.messageLayout);
            messageIv = itemView.findViewById(R.id.messageIv);

        }
    }

}
