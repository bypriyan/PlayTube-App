package com.bypriyan.m24.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bypriyan.m24.R;
import com.bypriyan.m24.model.ModelGroupChat;
import com.bypriyan.m24.utility.Constant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterGroupChatting  extends RecyclerView.Adapter<AdapterGroupChatting.MyHolder> {
    Context context;
    List<ModelGroupChat> groupChateList;
    private static final int MSG_TYPE_LEFT= 0;
    private static final int MSG_TYPE_RIGHT= 1;

    private FirebaseAuth firebaseAuth;

    public AdapterGroupChatting(Context context, List<ModelGroupChat> groupChateList) {
        this.context = context;
        this.groupChateList = groupChateList;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @NotNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int i) {
        if(i==MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.row_group_chat_right, parent, false);
            return new MyHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.row_group_chat_left, parent, false);
            return new MyHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterGroupChatting.MyHolder holder, int position) {

        ModelGroupChat modelGroupChate = groupChateList.get(position);

        String message = modelGroupChate.getMessage();
        String timestamp = modelGroupChate.getTimestamp();
        String senderUid = modelGroupChate.getSender();
        String messageType = modelGroupChate.getType();


        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();

        if(messageType.equals("text")){

            holder.messageTv.setVisibility(View.VISIBLE);
            holder.messageIv.setVisibility(View.GONE);
            holder.messageTv.setText(message);
        }else{
            holder.messageTv.setVisibility(View.GONE);
            holder.messageIv.setVisibility(View.VISIBLE);

            try {

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


        holder.time.setText(dateTime);
        setUserName(modelGroupChate, holder);

    }

    private void setUserName(ModelGroupChat modelGroupChate, MyHolder holder) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_USERS);
        reference.orderByChild(Constant.KEY_UID).equalTo(modelGroupChate.getSender()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                for(DataSnapshot ds: snapshot.getChildren()){
                    String name = ""+ds.child(Constant.KEY_NAME).getValue();

                    holder.nameTv.setText(name);

                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }


    @Override
    public int getItemCount() {
        return groupChateList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(groupChateList.get(position).getSender().equals(firebaseAuth.getUid())){
            return MSG_TYPE_RIGHT;

        }else{
            return MSG_TYPE_LEFT;

        }
    }

    class MyHolder extends RecyclerView.ViewHolder{

        TextView nameTv, messageTv, time;
        ImageView messageIv;

        public MyHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.nameTv);
            messageTv = itemView.findViewById(R.id.messageTv);
            time = itemView.findViewById(R.id.time);
            messageIv = itemView.findViewById(R.id.messageIv);
        }
    }
}
